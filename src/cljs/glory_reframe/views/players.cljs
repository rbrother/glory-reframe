(ns glory-reframe.views.players
  (:require [clojure.string :as str]
            [glory-reframe.ac :as ac]
            [glory-reframe.races :as races]
            [glory-reframe.views.html :as html]
            [glory-reframe.ships :as ships]
            [glory-reframe.utils :as utils]) )

(defn- fighter-image [ race-id ] [ :img { :src (ships/ship-image-url :fi race-id) } ] )

(defn- player-flag [ race-id ] [ :img {:src (str html/resources-url "FlagWavy/Flag-Wavy-" (name race-id) ".png")} ] )

(defn- player-row-data [ { race-id :glory-reframe.races/id tg :tg ac :ac pc :pc cc :command-pool sa :strategy-alloc fs :fleet-supply
                         strategies :strategies player-planets :planets player-systems :systems } ]
  ;  {:post [ (do (println (utils/pretty-pr %)) true) ] }
    (into [ :tr ]
          (html/td-items
            [(apply min (map :order strategies))
             (into [:div] (->> strategies (map :id) (map (fn [s] [:div s]))))
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
             (count pc)])))

(def players-header-items [ "Init" "Strategy" "Race" "Color" "Symbol" "VP" "CC"
                           "FS" "SA" "Systems" "Planets" "Res" "Inf" "TG" "Army Res"
                           "Tech" "AC" "PC" ])

(def players-header (into [:tr] (html/td-items players-header-items)))

(defn players-table [ amended-players ]
  (let [ rows (map player-row-data amended-players) ]
    [:table { :class "data" } (into [ :tbody ] (cons players-header rows))]  ))

(defn- ac-to-html [ id ]
  (let [ { description :description play :play set :set } (ac/all-ac-types id) ]
    [:div (str/capitalize (name id))
     (html/color-span "#909090" (str ": " description " Play: " play))]   ))

(defn- planet-to-html [ { id :id fresh :fresh res :res inf :inf } ]
  [:div (str/capitalize (name id)) " - " (html/color-span "green" res)
    " + " (html/color-span "#ff4040" inf) " - "
    (if fresh (html/color-span "#00ff00" "Ready") (html/color-span "#808080" "Exhausted"))  ]   )

(defn player-html [ role { race-id :glory-reframe.races/id acs :ac planets :planets } ]
  { :pre [ (not (nil? race-id)) ] }
  (let [ show-all (or (= role :game-master) (= role race-id))
        race (races/all-races race-id) ]
    [ :div
     [ :h3 (race :name) " - " (name race-id)
      (fighter-image race-id) (player-flag race-id) ]
     [ :div { :style { :margin-left "1cm" }  }
      [ :p "Strategy cards: xxx, yyy" ]
      [ :p (count acs) " Action cards: "
        (if show-all (map ac-to-html acs) "(hidden)") ]
      "Planets" (html/ol (map planet-to-html planets))
      "Tech" (html/ol ["a" "b" "c"])
      ]  ]    ))
