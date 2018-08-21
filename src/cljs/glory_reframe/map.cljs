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

(spec/def ::planet (spec/keys :req-un [ ::id ] :opt-un [ ::controller ::fresh ] ))

(spec/def ::board-piece (spec/keys :req [ ::logical-pos ::system ] ))

(spec/def ::board (spec/map-of keyword? ::board-piece ) )

(spec/def ::map (spec/map-of keyword? ::board-piece ) )

(spec/def ::player (spec/keys :req [ :glory-reframe.races/id ]))

(spec/def ::players (spec/map-of keyword? ::player ) )

(spec/def ::game-state (spec/keys :req [ ::map ::planets ] :req-un [ ::players ]))

(spec/def ::app-db (spec/keys :req-un [ ::game-state ]))

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

; eg. :abyz-fria -> :a3
(defn get-system-loc [ board system-id ]
  (some (fn [ [ loc-id { system ::system } ] ] (if (= system system-id) loc-id nil)) board))

; eg. :fria -> :a3
(defn find-planet-loc [ board planet ]
  (let [system-id (->> systems/all-planets-map (planet) :system-id)]
    (get-system-loc board system-id)   ))

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

(spec/fdef swap-system :args (spec/cat :board :glory-reframe.map/board
                                       :pars (spec/tuple keyword? keyword?) ))

(defn random-systems [ board & opt ]
  (let [ planet-system-ratio (or (first opt) 0.70)
         systems (systems/pick-random-systems (count board) planet-system-ratio)
         systems-and-locs (map vector (keys board) systems ) ]
    (reduce swap-system board systems-and-locs)))

