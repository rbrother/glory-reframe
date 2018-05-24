(ns glory-reframe.ships
  (:require [clojure.string :as string]
            [clojure.spec.alpha :as spec]
            [glory-reframe.map :as board]
            [glory-reframe.views.html :as html]
            [glory-reframe.views.svg :as svg]
            [glory-reframe.races :as races]
            [glory-reframe.utils :as utils]    ))

(def all-unit-types-arr
  [ { :id :fi :type :ship :name "Fighter"     :individual-ids false :image-name "Fighter"   :image-size [ 50 36 ] }
    { :id :de :type :ship :name "Destroyer"   :individual-ids true  :image-name "Destroyer" :image-size [ 42 56 ] }
    { :id :cr :type :ship :name "Cruiser"     :individual-ids true  :image-name "Cruiser"   :image-size [ 55 105 ] }
    { :id :ca :type :ship :name "Carrier"     :individual-ids true  :image-name "Carrier"   :image-size [ 50 139 ] }
    { :id :dr :type :ship :name "Dreadnaught" :individual-ids true  :image-name "Dreadnaught" :image-size [ 79 159 ] }
    { :id :ws :type :ship :name "Warsun"      :individual-ids true  :image-name "Warsun"    :image-size [ 135 113 ] }
    { :id :fl :type :ship :name "Flagship"    :individual-ids true  :image-name "Flagship"  :image-size [ 200 71 ] }
    { :id :gf :type :ground :name "Ground Force" :individual-ids false :image-name "GF"     :image-size [ 48 57 ] }
    { :id :st :type :ground :name "Shocktroop" :individual-ids false :image-name "ST"       :image-size [ 48 57 ] }
    { :id :mu :type :ground :name "Mechanised Unit" :individual-ids false :image-name "MU"  :image-size [ 75 36 ] }
    { :id :pds :type :ground :name "Planetary Defence System" :individual-ids false  :image-name "PDS" :image-size [ 67 49 ] }
    { :id :sd :type :ground :name "Spacedock" :individual-ids false  :image-name "Spacedock" :image-size [ 76 78 ] }
   ; Special tokens (move elsewhere..?)
    { :id :flag :type :special :image-name "Flag" :image-size [ 76 43 ] } ] )

(def all-unit-types (utils/index-by-id all-unit-types-arr))

(defn valid-unit-type? [ type ] (contains? all-unit-types type))

(spec/fdef valid-unit-type? :args (spec/cat :type (spec/or :type keyword? :count int?)))

;----------------------- units rendering ----------------------

(defn ship-image-url [ type race ]
  { :pre [ (valid-unit-type? type) ] }
  (if (= type :flag)
    (str html/resources-url "FlagWavy/Flag-Wavy-" (name race) ".png")
    ; Regular unit types
    (let [{image-name :image-name} (all-unit-types type)
          {color :unit-color} (races/all-races race)]
      (str html/resources-url "Ships/" color "/Unit-" color "-" image-name ".png"))))

(defn svg [ { id :id type :type race :owner count :count } loc ]
  { :pre [ (valid-unit-type? type)
           (not (nil? race))
           (contains? races/all-races race) ] }
  (let [ { [ width height :as tile-size ] :image-size individual-ships? :individual-ids } (all-unit-types type)
         center-shift [ 0 (* -0.5 height) ] ; Only need to center vertically. Horizontal centering done at group level
         final-loc (map round-any (map + center-shift loc))
         id-label (fn [] (svg/double-text (string/upper-case (name id)) [ 0 (+ 20 height) ] { :size 20 }))
         count-label (fn [ count ] (svg/double-text (str count) [ width 40 ] { :size 45 } )) ]
    (svg/g { :translate final-loc }
       (concat [ (svg/image [0 0] tile-size (ship-image-url type race) (str "unit-" (name (or id type))) ) ]
               (cond
                 individual-ships? [ (id-label) ]
                 (and count (> count 1)) [ (count-label count) ]
                 :else [ ] )))))

(defn width [ { type :type count :count :as ship } ]
  (let [ { [ image-width _ ] :image-size } (all-unit-types type)
         count-label-width (if count 30 0) ]
    (+ image-width count-label-width)))

(defn group-width [ ships ] (apply + (map width ships)))

(defn group-svg [ [ group [ x y :as loc ] ] ] ; returns [ [:g ... ] [:g ... ] ... ]
  (if (empty? group) []
    (let [ { type :type count :count :as ship } (first group)
           next-loc [ (+ x (width ship) 1) y ] ]
      (conj (group-svg [ (rest group) next-loc ] )
            (svg ship loc)))))

;-------------------- units commands (private) --------------------------

(defn- new-unit-index [ game-state type ]
  (inc (get-in game-state [ :ship-counters type ] 0)))

; Resolves into { :location :a1 :planet :aah } (planet can be also nil)
(defn- resolve-location [ loc unit-type { board :map planets :planets :as game } ]
  (let [ ship? (= :ship ((all-unit-types unit-type) :type))
         is-system? (contains? (set (board/all-systems game)) loc) ]
    (cond
      ; :a1 (system-loc) for ships
      (and ship? (board loc)) { :location loc }
      ; :aah, :arinam-meer (system-id) for ships
      (and ship? is-system?) { :location (board/get-system-loc board loc) }
      ; :aah (planet-id) for ground-units
      (and (not ship?) (planets loc)) { :location (board/find-planet-loc board loc) :planet loc }
      ; :a1 (system-loc) for ground-units when only 1 planet in the system
      (and (not ship?) (board loc)) { :location loc :planet (first ((board loc) :planets)) }
      :else (throw (Exception. (str "Cannot resolve location " loc " for unit type " unit-type)))  )))

(defn- new-unit [ unit-id loc-id owner type game ]
  (merge { :id unit-id :owner owner :type type }
        (resolve-location loc-id type game) ))

(defn- add-new-unit [ loc-id owner type game ]
  { :pre [ (valid-unit-type? type) ] }
  (let [ idx (new-unit-index game type)
         unit-id (keyword (str (name type) idx)) ]
    (-> game
        (assoc-in [ :ship-counters type ] idx )
        (assoc-in [ :map loc-id :controller ] owner )
        (update :units assoc unit-id (new-unit unit-id loc-id owner type game))  )))

(defn- operate-on-units [ game units-fn unit-ids ]
  (assert (not (empty? unit-ids)) "No units found")
  (-> game
      (update :units (fn [units] (reduce units-fn units unit-ids))))   )

(defn- del-unit [ units id ] (dissoc units id))

(defn- move-unit [ units-map unit-id loc-id game ]
  (update units-map unit-id (fn [unit] (merge unit (resolve-location loc-id (:type unit) game))))  )

;-------- units commands (public) -----------

(defn new-units [ loc-id owner types game ]
  (let [ new-unit-of (fn [ new-game-state type] (add-new-unit loc-id owner type new-game-state )) ]
    (reduce new-unit-of game types)))

(defn del-units [ ids game ]
  (operate-on-units game del-unit ids ))

(defn move-units [ unit-ids loc-id player game ]
  (let [ move-unit-to (fn [ units-map unit-id ] (move-unit units-map unit-id loc-id game)) ]
    (-> game
        (assoc-in [ :map loc-id :controller ] player )
        (operate-on-units move-unit-to unit-ids))))
