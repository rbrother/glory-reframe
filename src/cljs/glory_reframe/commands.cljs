(ns glory-reframe.commands
  (:require [glory-reframe.utils :as utils]
            [glory-reframe.map :as board]
            [glory-reframe.ships :as ships]
            [glory-reframe.ac :as ac]))

; There commands do not actually do anything, but they
; return game-updating func for the specified command

;------------ command helpers ---------------

; The whole planet-list is re-created when board is modified, so board modification
; should not be done after game starts and there is some info on planets
(defn- update-planets [ { board :glory-reframe.map/map :as game } ]
  (let [ all-planets
        (->>  board (vals) (map :glory-reframe.map/planets)
              (map seq) (flatten) (remove nil?)
              (map (fn [id] { :id id :controller nil :fresh true }))  ) ]
    (assoc game :glory-reframe.map/planets (utils/index-by-id all-planets))  ))

(defn- board-command [ board-func ]
  (fn [ game & pars ] (update-planets (update game :glory-reframe.map/map board-func))))

(defn- make-board-command [ new-board ]
  (fn [ game & pars ] (update-planets (merge game { :glory-reframe.map/map new-board :ship-counters {} } ))))

(defn player? [ { players :players } player ] (contains? players player))

(defn- player-optional-command "Command for which player role can be given as first parameter (otherwise use role)"
  [ [ possible-player & rest-pars :as pars ] inner-fn]
  (fn [ game role]
    (if (player? game (keyword possible-player))
      (inner-fn game role (keyword possible-player) rest-pars)
      (do (assert (not= role :game-master) "GM Must specify player for the command")
          (inner-fn game role role pars)))))

;----------- map commands --------------------

(defn round-board
  ( [] (round-board 3) )
  ( [ size ]
   (make-board-command (board/round-board size))))

(defn rect-board
  ( [] (rect-board 3 3) )
  ( [ width height ]
   (make-board-command (board/rect-board width height))))

(defn set-systems-random [ & opt ] (board-command #(apply board/random-systems % opt)))

(defn set-system [ loc-id system-id ]
  (board-command #(board/swap-system % [ (keyword loc-id) (keyword system-id) ] )))

(defn del-system [ loc-id ]
  (board-command #(dissoc % loc-id)))

;------------- players -----------------

(defn- set-players-inner [ game player-ids-and-passwords ]
  (assoc game :players
              (->> player-ids-and-passwords
                   (partition 2)
                   (map (fn [ [id pwd] ] { :id id :password pwd :ac [] :pc [] } ))
                   (utils/index-by-id))))

(defn set-players [ & player-ids-and-passwords ]
  (fn [ game & pars ] (set-players-inner game player-ids-and-passwords)))

;------------ unit commands ------------------

(defn- resolve-unit-types [ [ a b & others :as types ] ]
  (let [item (keyword a)]
    (cond (not a) '()
          (ships/valid-unit-type? item) (cons item (resolve-unit-types (next types)))
          (number? a) (concat (take a (repeat (keyword b))) (resolve-unit-types others))
          :else (throw (str "Unit type unknown " a)))))

(defn- filter-units [ attr value units-map ]
  (->> units-map (vals) (filter #(= (% attr) value)) (utils/index-by-id)) )

; units-defs can be combination of (1) unit-ids eg. :ws3 , (2) unit types eg. :gf, (3) count + type eg. 3 :gf.
; returns list of unit-id:s
(defn- resolve-unit-ids [ [ a b & others :as unit-defs ] available-units]
  (let [ item (keyword a) ]
    (cond (not a) '()
          (= item :from) (resolve-unit-ids others (filter-units :location (keyword b) available-units))
          (= item :all) (keys available-units)
          (contains? available-units item)           ; unit-id eg. :ws3
            (cons item (resolve-unit-ids (next unit-defs) (dissoc available-units item)))
          (ships/valid-unit-type? item)              ; unit-type eg. :ca
            (let [unit-id (->> available-units (filter-units :type item) (keys) (sort) (first))]
              (assert unit-id (str "Unit of type " item " not found from the location"))
              (cons unit-id (resolve-unit-ids (next unit-defs) (dissoc available-units unit-id))))
          (number? a)                                       ; count and type eg. 3 :gf. Expand to :gf :gf :gf
            (resolve-unit-ids (concat others (take a (repeat (keyword b)))) available-units)
          :else (throw (str "Unit definition unknown " a)))))

(defn- resolve-unit-ids-outer [ unit-defs available-units player]
  (resolve-unit-ids unit-defs (filter-units :owner player available-units)))

; ----------

(defn new [ & pars]
  (player-optional-command pars
     (fn [ game _ player pars]
       (let [loc-id (last pars) types (drop-last pars)]
         (ships/new-units (keyword loc-id) player (resolve-unit-types types) game)))))

; CHeck that can move only own units, include role
(defn del [ & pars]
  (player-optional-command pars
     (fn [ { all-units :units :as game } role player units]
       (let [ unit-ids (resolve-unit-ids-outer units all-units player)]
         (ships/del-units unit-ids game)))))

; CHeck that can move only own units, include role
(defn move [ & pars]
  (let [ dest (last pars), pre-pars (drop-last pars)]
    (player-optional-command pre-pars
       (fn [{all-units :units :as game} role player units]
         (let [unit-ids (resolve-unit-ids-outer units all-units player)]
           (ships/move-units unit-ids (keyword dest) player game))))))

;----------- activation of systems ------------------

(defn activate [ & pars ]
  (player-optional-command pars
     (fn [ game _ player [ location ] ]
       { :pre [ (-> game :map location) ] }
       (assert (> (-> game :players player :command-pool) 0))
       (-> game
           (update-in [ :map location :activated ] assoc player true )
           (update-in [ :players player :command-pool ] dec)     ))))

;----------- card-commands commands ------------------

(defn ac-deck-create "Adds a fresh shuffled pack of AC:s to the game" []
  (fn [ game role ] (-> game (assoc :ac-deck (ac/create-ac-deck)))))

(defn ac-get "get AC from deck to a player" [& pars]
  (player-optional-command pars
     (fn [ game role player pars]
       (let [ card (first (:ac-deck game))]
         (-> game
             (update-in [:ac-deck] rest)
             (update-in [:players player :ac] conj card))))))

(defn ac-play [ & pars ] nil   "Move AC from player to discard"
  (player-optional-command pars
     (fn [ game role player [ ac ]]
       (assert (-> game :players player :ac (utils/list-contains? ac)) (str "AC " ac " not found from player " player))
       (-> game
           (update-in [ :players player :ac ] utils/remove-single ac)
           (update-in [ :ac-discard ] conj ac)))))

;----------- high-level commands ------------------

(defn start-game
  "Adds players starting units to their home-systems, shuffles new pack of AC and PC and marks round 1 started"
  []
  (fn [ game role ]
    (-> game
        (assoc :ac-deck (ac/create-ac-deck))   )))
