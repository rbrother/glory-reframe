(ns glory-reframe.views.players
  (:require [clojure.string :as str]
            [glory-reframe.utils :as utils]
            [glory-reframe.ac :as ac]
            [glory-reframe.systems :as systems]
            [glory-reframe.strategies :as strategies]
            [glory-reframe.races :as races]
            [glory-reframe.views.html :as html]
            [glory-reframe.ships :as ships]) )

(defn- fighter-image [ race-id ] [ :img { :src (ships/ship-image-url :fi race-id) } ] )

(defn- player-flag [ race-id ] [ :img {:src (str html/resources-url "FlagWavy/Flag-Wavy-" (name race-id) ".png")} ] )

(defn- player-controls [race-id]
  (fn [ { controller :controller owner :owner } ]
    (or (= controller race-id) (= owner race-id) )))

(defn filter-player [ race-id items ] (filter (player-controls race-id) items))

(defn- player-row-data [ board planets
                        { race-id :id tg :tg ac :ac pc :pc cc :command-pool sa :strategy-alloc fs :fleet-supply
                         strategies :strategies } ]
  (let [ player-systems (filter-player race-id board)
        player-planets (filter-player race-id planets) ]
    [(apply min (map :order strategies))
     (->> strategies (map :id) (map (fn [s] [ :div s ] )))
     (str/capitalize (name race-id))
     (fighter-image race-id)
     (player-flag race-id)
     "VP"
     cc
     fs
     sa
     (count player-systems)
     (count player-planets)
     (->> player-planets (map :res) (reduce +))
     (->> player-planets (map :inf) (reduce +))
     (or tg 0)
     "Army Res"
     "Techs"
     (count ac)
     (count pc)   ]))

(def players-header-items [ "Init" "Strategy" "Race" "Color" "Symbol" "VP" "CC"
                           "FS" "SA" "Systems" "Planets" "Res" "Inf" "TG" "Army Res"
                           "Tech" "AC" "PC" ])

(def players-header [:tr (html/td-items players-header-items)])

(defn players-table [ amended-players board amended-planets ]
  (let [ rows (map #(player-row-data board amended-planets %) amended-players) ]
    (println rows)
    [:table { :class "data" } [:tbody (cons players-header rows)]]   ))

(defn- ac-to-html [ id ]
  (let [ { descr :description play :play set :set } (ac/all-ac-types id) ]
    (list (str/capitalize (name id))
          (html/color-span "#909090" (str ": " descr " Play: " play)))  ))

(defn- planet-to-html [ { id :id fresh :fresh res :res inf :inf } ]
  (list (str/capitalize (name id)) " - " (html/color-span "green" res)
        " + " (html/color-span "#ff4040" inf) " - "
        (if fresh (html/color-span "#00ff00" "Ready") (html/color-span "#808080" "Exhausted")))  )

(defn- player-html [ role all-planets { race-id :id acs :ac } ]
  { :pre [ (not (nil? race-id)) ] }
  (let [ show-all (or (= role :game-master) (= role race-id))
        race (races/all-races race-id)
        planets (filter-player race-id all-planets) ]
    (list
      [ :h3 (race :name) " - " (name race-id)
       (fighter-image race-id) (player-flag race-id) ]
      [ :div { :style "margin-left: 1cm;" }
       [ :p "Strategy cards: xxx, yyy" ]
       (if show-all
         (list "Action Cards: " (count acs) (html/ol (map ac-to-html acs)))
         "(hidden)")
       "Planets" (html/ol (map planet-to-html planets))
       "Tech" (html/ol ["a" "b" "c"])
       ]  )))

(defn amend-player [ { player-id :id :as player } strategies ]
  (assoc player :strategies (filter-player player-id strategies)))

(defn players-html [ { planets :planets strat :strategies players :players board :map } role ]
  (let [amended-planets (clojure.set/join (vals planets) systems/all-planets-set)
        amended-strategies (clojure.set/join strat strategies/all-strategies-arr)
        sorted-strategies (sort-by :order amended-strategies)
        player-order (->> sorted-strategies (map :owner) (filter identity) distinct)
        players-in-order (->> player-order (map #(players %)))
        amended-players (->> players-in-order (map #(amend-player % amended-strategies))) ]
    [:div
     (players-table amended-players (vals board) amended-planets)
     (map (partial player-html role amended-planets) amended-players)]))
