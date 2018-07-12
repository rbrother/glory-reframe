(ns glory-reframe.views.map-svg
  (:require [clojure.string :as str]
            [glory-reframe.utils :as utils]
            [glory-reframe.systems :as systems]
            [glory-reframe.ships :as ships]
            [glory-reframe.views.svg :as svg]
            [glory-reframe.map]
            [clojure.spec.alpha :as spec]
            [clojure.spec.test.alpha :as stest]) )

(defn- default-ship-locs [ planets-count ship-count ]
  (cond
    (= planets-count 0)
      (cond
        (<= ship-count 4) [ [ 0 0 ] ]
        :else [ [ 0 -80 ] [ 0 80 ] ] )
    (= planets-count 1)
      (cond
        (<= ship-count 2) [ [ 120 0 ] ]
        (<= ship-count 4) [ [ 120 0 ] [ 0 -120 ] ]
        :else [ [ 120 0 ] [ 0 -120 ] [ -120 0 ] ] )
    (= planets-count 2)
      (cond
        (<= ship-count 6) [ [ -90 80 ] [ 90 -80 ] ]
        :else [ [ -90 80 ] [ 90 -80 ] [ 0 0 ] ] )
    (= planets-count 3)
      [ [ 0 0 ] [ 90 -80 ] [ -90 -90 ] [ 90 110 ] ] ))

; allow ship-count param to be compatible with default-ship-locs
(defn planet-units-locs [ { id :id planet-loc :loc :as planet } ship-count ]
  (if planet-loc
    (map (fn [ loc ] (map + loc planet-loc)) [ [ 0 -30 ] [ 0 30 ] ] )
    (throw (str "Planet does not define location info " id))))

(defn center-group-to-loc "Moves group of ships in given position to left to center the group horizontally"
  [ group [ x y ] ]
      [ group [ (+ x (* -0.5 (ships/group-width group))) y ] ] )

(defn group-units "Groups seq of ships to equally-sized groups in given location positions"
  [ group-locs-func units ]
    (let [ group-locs (group-locs-func (count units))
           units-per-group (int (Math/ceil (/ (count units) (count group-locs))))
           unit-groups (partition units-per-group units-per-group [] units) ]
      (map center-group-to-loc unit-groups group-locs)))

(defn- collapse-group-id "Fighters, gf, etc. with same collapse-group-id are grouped to single item with count"
  [ { type :type individual-id :id } ]
    (let [ { individual-ids :individual-ids } (ships/all-unit-types type) ]
      (if individual-ids individual-id type)))

(defn- collapse-group [ [ first & rest :as group ] ]
  (if (utils/single? group) first
    (-> first (assoc :count (count group)) (dissoc :id))))

(defn collapse-fighters "Allows showing multiple fighters (and GF etc.) as <Fighter><Count> instead of individual icons."
  [ ships ]
  (->> ships
       (sort-by :type)
       (partition-by collapse-group-id)
       (map collapse-group)))

(defn units-svg "Generates SVG for all ships in system (or units on planet) distributed to given group locations"
  [ [ units group-locs-func ] ]
  (->> units
       (collapse-fighters)
       (group-units group-locs-func)
       (mapcat ships/group-svg)))

(defn piece-to-background-svg [ { logical-pos :glory-reframe.map/logical-pos
                      system-id :glory-reframe.map/system loc-id :id } ]
  (let [ system-info (systems/get-system system-id)
        tile-label (svg/double-text (str/upper-case (name loc-id)) [ 25 200 ] { :id (str (name system-id) "-loc-label") })
        system-id (str (name system-id) "-system") ]
    (svg/g { :translate (systems/screen-loc logical-pos) :id system-id } [
        (svg/image [ 0 0 ] systems/tile-size (systems/image-url system-info) (str "system-" (name loc-id)) )
        tile-label ] )))

(defn planet-to-units-svg [ planet+ ground-units ]
  (let [ units-formation [ ground-units (partial planet-units-locs planet+) ]]
    (units-svg units-formation)))

(spec/fdef planet-to-units-svg
           :args (spec/cat :planet+ :glory-reframe.map/planet :units coll?) )

(defn piece-to-units-svg [ { planets :glory-reframe.map/planets } ships ]
  (let [ planet-count (count (or planets []))
         ships-formation [ships (partial default-ship-locs planet-count)]  ]
    (units-svg ships-formation)))

(defn render-map-background [ board ]
   (into [:g] (map piece-to-background-svg (vals board))))
