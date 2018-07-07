(ns glory-reframe.map
  (:require [clojure.spec.alpha :as spec]
            [glory-reframe.races]
            [glory-reframe.systems :as systems]
            [glory-reframe.utils :as utils]  ))

; -------------------------- map ---------------------------------

(spec/def ::logical-pos (spec/tuple int? int?) )            ; [ 2 -1 ]

(spec/def ::system keyword? )            ; :mecatol-rex

(spec/def ::fresh boolean?)

(spec/def ::id keyword?)

(spec/def ::controller (spec/or :race :glory-reframe.races/id :nil nil?))

(spec/def ::planet (spec/keys :req-un [ ::controller ::id ::fresh ] ))

(spec/def ::planets coll? )            ; #{ :torkan :tequran }

(spec/def ::board-piece (spec/keys :req [ ::logical-pos ::system ::planets ]))

(spec/def ::board (spec/map-of keyword? ::board-piece ) )

(spec/def ::map (spec/map-of keyword? ::board-piece ) )

(spec/def ::player (spec/keys :req [ :glory-reframe.races/id ]))

(spec/def ::players (spec/map-of keyword? ::player ) )

(spec/def ::game-state (spec/keys :req [ ::map ::planets ] :req-un [ ::players ]))

(spec/def ::app-db (spec/keys :req-un [ ::game-state ]))

(def test-map
  { :name "Sandbox"
    :command-to-execute nil
    :game-state {
                :ac-deck [ :military-foresight
                          :flank-speed
                          :direct-hit
                          :flank-speed
                          :council-dissolved
                          :rally-of-people
                          :synchronicity
                          :skilled-retreat
                          :enhanced-armor
                          :command-summit
                          :rare-mineral
                          :veto
                          :shields-holding
                          :privateers
                          :target-their-flagship
                          :in-silence-of-space
                          :morale-boost ]
                :counter 26
                :gm-password ""
                :history [   { :counter 1 }  ]
                ::map {   :a3 { :controller :hacan :id :a3 ::logical-pos [ -2 0 ] ::planets #{ :kobol } ::system :kobol }
                      :a4 { :activated { :norr true } :id :a4 ::logical-pos [ -2 1 ] ::planets #{ :coorneeq :resculon } ::system :coorneeq-resculon }
                      :a5 { :id :a5 ::logical-pos [ -2 2 ] ::planets #{ :kazenoeki } ::system :kazenoeki }
                      :b2 { :activated { :norr true } :controller :hacan :id :b2 ::logical-pos [ -1 -1 ] ::planets #{ :sorkragh :machall } ::system :machall-sorkragh }
                      :b3 { :id :b3 ::logical-pos [ -1 0 ] ::planets #{ :torkan :tequran } ::system :tequran-torkan }
                      :b4 { :id :b4 ::logical-pos [ -1 1 ] ::planets #{ :suuth } ::system :suuth }
                      :b5 { :controller :norr :id :b5 ::logical-pos [ -1 2 ] ::planets #{ :cicerus } ::system :cicerus }
                      :c1 { :controller :hacan :id :c1 ::logical-pos [ 0 -2 ] ::planets #{  } ::system :wormhole-b }
                      :c2 { :id :c2 ::logical-pos [ 0 -1 ] ::planets #{ :ptah } ::system :ptah }
                      :c3 { :id :c3 ::logical-pos [ 0 0 ] ::planets #{ :bellatrix :tsion } ::system :tsion-bellatrix }
                      :c4 { :id :c4 ::logical-pos [ 0 1 ] ::planets #{ :chnum } ::system :chnum }
                      :c5 { :id :c5 ::logical-pos [ 0 2 ] ::planets #{  } ::system :ion-storm }
                      :d1 { :id :d1 ::logical-pos [ 1 -2 ] ::planets #{ :parzifal } ::system :parzifal }
                      :d2 { :id :d2 ::logical-pos [ 1 -1 ] ::planets #{  } ::system :asteroid-field }
                      :d3 { :activated { :hacan true :naalu true } :id :d3 ::logical-pos [ 1 0 ] ::planets #{  } ::system :empty }
                      :d4 { :activated { :hacan true :naalu true } :id :d4 ::logical-pos [ 1 1 ] ::planets #{  } ::system :empty }
                      :e1 { :id :e1 ::logical-pos [ 2 -2 ] ::planets #{  } ::system :wormhole-a }
                      :e2 { :controller :naalu :id :e2 ::logical-pos [ 2 -1 ] ::planets #{ :martinez :sulla } ::system :sulla-martinez }
                      :e3 { :id :e3 ::logical-pos [ 2 0 ] ::planets #{ :sem-lore } ::system :sem-lore } }
                ::planets {
                          :bellatrix { :controller nil :id :bellatrix :fresh true }
                          :chnum { :controller nil :id :chnum :fresh true }
                          :cicerus { :controller :norr :id :cicerus :fresh true }
                          :coorneeq { :controller nil :id :coorneeq :fresh true }
                          :kazenoeki { :controller nil :id :kazenoeki :fresh true }
                          :kobol { :controller :hacan :id :kobol :fresh true }
                          :machall { :controller :hacan :id :machall :fresh true }
                          :martinez { :controller :naalu :id :martinez :fresh true }
                          :parzifal { :controller nil :id :parzifal :fresh true }
                          :ptah { :controller nil :id :ptah :fresh false }
                          :resculon { :controller nil :id :resculon :fresh false }
                          :sem-lore { :controller nil :id :sem-lore :fresh false }
                          :sorkragh { :controller :hacan :id :sorkragh :fresh false }
                          :sulla { :controller :naalu :id :sulla :fresh false }
                          :suuth { :controller nil :id :suuth :fresh false }
                          :tequran { :controller nil :id :tequran :fresh false }
                          :torkan { :controller nil :id :torkan :fresh false }
                          :tsion { :controller nil :id :tsion :fresh false } }
                :players {   :hacan { :ac [ :spacedock-accident ] :command-pool 0 :fleet-supply 3 :glory-reframe.races/id :hacan :password "abc" :pc [  ] :strategy-alloc 2 }
                          :naalu { :ac [ :plague :insubordination ] :command-pool 0 :fleet-supply 3 :glory-reframe.races/id :naalu :password "123" :pc [  ] :strategy-alloc 2 }
                          :norr { :ac [  ] :command-pool 1 :fleet-supply 4 :glory-reframe.races/id :norr :password "xyz" :pc [  ] :strategy-alloc 3 } }
                :strategies #{
                              { :id :leadership4 :owner :norr :ready true :bonus 0 }
                              { :id :diplomacy4 :owner :norr :ready false :bonus 0 }
                              { :id :politics4  :owner :naalu :ready true :bonus 0 }
                              { :id :production :ready true :bonus 0 }
                              { :id :trade4 :owner :hacan :ready false :bonus 0 }
                              { :id :warfare4 :owner :hacan :ready true :bonus 0 }
                              { :id :technology4 :owner :naalu :ready true :bonus 0 }
                              { :id :bureaucracy :ready true :bonus 2 }
                              }
                :ship-counters { :ca 3 :cr 2 :de 2 :fi 2 :gf 10 }
                :units {   :ca1 { :id :ca1 :location :b2 :owner :hacan :type :ca }
                        :ca2 { :id :ca2 :location :b2 :owner :hacan :type :ca }
                        :ca3 { :id :ca3 :location :e2 :owner :naalu :type :ca }
                        :cr1 { :id :cr1 :location :b2 :owner :hacan :type :cr }
                        :cr2 { :id :cr2 :location :b5 :owner :norr :type :cr }
                        :de1 { :id :de1 :location :b5 :owner :norr :type :de }
                        :de2 { :id :de2 :location :b5 :owner :norr :type :de }
                        :fi1 { :id :fi1 :location :e2 :owner :naalu :type :fi }
                        :fi2 { :id :fi2 :location :e2 :owner :naalu :type :fi }
                        :gf1 { :id :gf1 :location :a3 :owner :hacan :planet :kobol :type :gf }
                        :gf10 { :id :gf10 :location :e2 :owner :naalu :planet :sulla :type :gf }
                        :gf2 { :id :gf2 :location :a3 :owner :hacan :planet :kobol :type :gf }
                        :gf3 { :id :gf3 :location :b4 :owner :hacan :planet :suuth :type :gf }
                        :gf4 { :id :gf4 :location :b4 :owner :hacan :planet :suuth :type :gf }
                        :gf5 { :id :gf5 :location :b5 :owner :norr :planet :cicerus :type :gf }
                        :gf6 { :id :gf6 :location :b5 :owner :norr :planet :cicerus :type :gf }
                        :gf7 { :id :gf7 :location :e2 :owner :naalu :planet :sulla :type :gf }
                        :gf8 { :id :gf8 :location :e2 :owner :naalu :planet :sulla :type :gf }
                        :gf9 { :id :gf9 :location :e2 :owner :naalu :planet :sulla :type :gf } } } }  )


(def good-letters [ "a", "b", "c", "d", "e", "f", "g",
              "h", "j", "k", "m", "n", "p",
              "r", "s", "t", "u", "v", "z", "y", "z" ] )

(defn location-id [ [ x y ] [ min-x min-y ] ]
  (keyword (str (good-letters (- x min-x)) (+ 1 (- y min-y)))))

(defn bounding-rect [ map-pieces ]
  (let [ screen-locs (->> map-pieces
                          (map ::logical-pos)
                          (map systems/screen-loc)) ]
    [ (utils/min-pos screen-locs) (map + (utils/max-pos screen-locs) systems/tile-size) ] ))

(spec/fdef bounding-rect
           :args (spec/cat :map-pieces (spec/coll-of :glory-reframe.map/board-piece)))

(defn- logical-distance [ [ logical-x logical-y ] ]
  (let [ abs-x (Math/abs logical-x)
         abs-y (Math/abs logical-y) ]
    (if (pos? (* logical-x logical-y)) (+ abs-x abs-y) (max abs-x abs-y))))

(def setup-tiles [ :setup-red :setup-yellow :setup-light-blue :setup-medium-blue :setup-dark-blue ] )

(defn- setup-system [ pos tile-index ]
  { ::logical-pos pos
    ::system (nth setup-tiles (mod tile-index 5)) } )

(defn amend-tile-ids [ map-pieces ]
  (let [ min-loc (utils/min-pos (map ::logical-pos map-pieces))
         amend-tile-id (fn [tile] (assoc tile :id (location-id (::logical-pos tile) min-loc))) ]
    (map amend-tile-id map-pieces)))

(defn- make-board [ initial-range-size piece-filter ]
  (let [ a-range (range (- initial-range-size) (inc initial-range-size)) ]
    (->> (utils/range2d a-range a-range)
         (filter piece-filter)
         (map (fn [ pos ] (setup-system pos (logical-distance pos))))
         (amend-tile-ids)
         (utils/index-by-id))))

(defn round-board [ rings ]
  (make-board rings (fn [ pos ] (< (logical-distance pos) rings))))

(defn rect-board [ width height ]
  (let [ [ tile-width tile-height ] systems/tile-size
         pixel-size [ (* width tile-width 0.75) (* height tile-height) ]
         bounding-rect [ (utils/mul-vec pixel-size -0.5) (utils/mul-vec pixel-size 0.5) ] ]
    (make-board (+ width height) (fn [ pos ] (utils/inside-rect? (systems/screen-loc pos) bounding-rect)))))

;-------------------- map query --------------------------

(defn get-loc-of [ board system-predicate ]
  (ffirst (filter (fn [ [ system-id system ] ] (system-predicate system)) board)))

; eg. :abyz-fria -> :a3
(defn get-system-loc [ board system-id ]
  (get-loc-of board (fn [ { system ::system } ] (= system system-id))))

; eg. :fria -> :a3
(defn find-planet-loc [ board planet ]
  { :post [ (not (nil? %)) ] }
  (get-loc-of board (fn [ { planets ::planets } ] (and planets (contains? planets planet)))))

(defn all-systems [ game ] (map ::system (vals (game :map))))

;------------------- map piece operations -------------------------

(defn swap-piece-system [ piece system-id ]
  { :pre [ (keyword? system-id) ]}
  (let [ planets ((systems/get-system system-id) ::planets)
         ; Keep only planets names in the map, don't drag along static planet-info
         ; that we gan get from all-systems map when needed. This keeps the test-data manageable.
         planet-ids (if planets (keys planets) [] ) ]
    (merge piece { ::system system-id ::planets (set planet-ids) } )))

(defn swap-system [ board [ loc-id system-id ] ]
  { :pre [ (contains? board loc-id) (keyword? system-id) ] }
  (update-in board [ loc-id ] swap-piece-system system-id))

(defn random-systems [ board & opt ]
  (let [ planet-system-ratio (or (first opt) 0.70)
         systems (systems/pick-random-systems (count board) planet-system-ratio)
         systems-and-locs (map vector (keys board) systems ) ]
    (reduce swap-system board systems-and-locs)))

