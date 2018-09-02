(ns glory-reframe.views
  (:require [re-frame.core :refer [subscribe dispatch]]
            [glory-reframe.views.map-svg :as map-svg]
            [glory-reframe.views.players :as players]
            [glory-reframe.views.command-page :as command-page]
            [glory-reframe.utils :as utils]
            [glory-reframe.views.svg :as svg]
            [glory-reframe.systems :as systems]))

(defn board-background []
  (let [ board @(subscribe [:board] )]
    (map-svg/render-map-background board)   ))

(defn planet-units [ piece-id [ planet-id { planet-loc :loc } ] ]
  (let [ figures @(subscribe [:pieces-to-render-at-planet piece-id planet-id])]
    (map-svg/units-svg figures (partial map-svg/planet-units-locs planet-loc))    ))

(defn board-piece-units [ piece-id ]
  (let [ board-piece @(subscribe [ :board-piece piece-id ] )
         { logical-pos :glory-reframe.map/logical-pos system-id :glory-reframe.map/system } board-piece
         { planets :planets } (systems/all-systems system-id)
         planet-count (count (or planets []))
         space-figures @(subscribe [ :pieces-to-render-at piece-id ] )
         center (utils/mul-vec systems/tile-size 0.5) ]
    (svg/g {:translate (map + (systems/screen-loc logical-pos) center)}
           (concat (map-svg/units-svg space-figures (partial map-svg/default-ship-locs planet-count))
                   (mapcat (partial planet-units piece-id) planets)   ))))

(defn board-units []
  (let [ piece-ids @(subscribe [:board-piece-ids]) ]
    (into [:g] (map (fn [id] [ board-piece-units id ]) piece-ids))    ))

(defn board
  ( [ ] (board {}) )
  ( [ opts ]
   (println "rendering: board")
    (if (number? opts) (board { :scale opts })
      (let [ board* @(subscribe [:board])
            map-pieces (vals board*)
            scale (or (:scale opts) 0.5)
            [ min-corner max-corner :as bounds] (glory-reframe.map/bounding-rect map-pieces)
            svg-size (utils/mul-vec (utils/rect-size bounds) scale) ]
        (svg/svg svg-size
           (svg/g { :scale scale :translate (utils/mul-vec min-corner -1.0) }
                  [ [ board-background ]
                    [ board-units] ]      ))))))

(defn players-table [ ]
  (println "rendering: players-table")
  (let [ players @(subscribe [:players]) ]
    (players/players-table players) ))

(defn players-list []
  (println "rendering: players-list")
  (let [ players @(subscribe [:players] )
        role :game-master ]
    (into [:div]
          (map (partial players/player-html role) players))))

(defn game-panel []
  [:div
   [ :h1 {:style {:text-align "center"}} "---- " @(subscribe [:game-name]) " ----"]
   [ command-page/command-entering ]
   [ board ]         ; Could call (board) also directly, but this "react-way" improves performance if only part of content change
   [ players-table ]
   [ players-list ] ] )

(defn main-panel []
  #_{:post [ (do (println (utils/pretty-pr %)) true) ] }
  (let [ game-name @(subscribe [:game-name])]
    (if game-name [game-panel]
                  [:div "No game loaded "
                   [:button { :on-click #(dispatch [:load-game]) }
                    "Load Game"]  ])))
