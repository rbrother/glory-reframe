(ns glory-reframe.views
  (:require [re-frame.core :as re-frame]
            [glory-reframe.views.map-svg :as map-svg]
            [glory-reframe.views.players :as players]
            [glory-reframe.views.command-page :as command-page]
            [glory-reframe.utils :as utils]
            [glory-reframe.views.svg :as svg]
            [glory-reframe.systems :as systems]))

(defn board-background []
  (let [ board @(re-frame/subscribe [:board] )]
    (map-svg/render-map-background board)   ))

(defn planet-units [ piece-id planet-id ]
  (let [ { planet-loc :loc } @(re-frame/subscribe [:planet planet-id])
        units @(re-frame/subscribe [:ground-units-at piece-id planet-id])]
    (map-svg/units-svg units (partial map-svg/planet-units-locs planet-loc))    ))

(defn board-piece-units [ piece-id ]
  #_{:post [ (do (println (utils/pretty-pr %)) true) ] }
  (let [ piece @(re-frame/subscribe [ :board-piece piece-id ] )
         planet-count (count (get piece :glory-reframe.map/planets []))
        space-figures @(re-frame/subscribe [ :pieces-to-render-at piece-id ] )
        { logical-pos :glory-reframe.map/logical-pos planets :glory-reframe.map/planets } piece
        center (utils/mul-vec systems/tile-size 0.5) ]
    (svg/g {:translate (map + (systems/screen-loc logical-pos) center)}
           (concat (map-svg/units-svg space-figures (partial map-svg/default-ship-locs planet-count))
                   (mapcat (partial planet-units piece-id) planets)   ))))

(defn board-units []
  (let [ piece-ids @(re-frame/subscribe [:board-piece-ids]) ]
    (into [:g] (map (fn [id] [ board-piece-units id ]) piece-ids))    ))

(defn board
  ( [ ] (board {}) )
  ( [ opts ]
   #_{:post [ (do (println (utils/pretty-pr %)) true) ] }
   (println "rendering: board")
    (if (number? opts) (board { :scale opts })
      (let [ board* @(re-frame/subscribe [:board])
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
  (let [ players @(re-frame/subscribe [:players]) ]
    (players/players-table players) ))

(defn players-list []
  (println "rendering: players-list")
  (let [ players @(re-frame/subscribe [:players] )
        role :game-master ]
    (into [:div]
          (map (partial players/player-html role) players))))

(defn main-panel []
  #_{:post [ (do (println (utils/pretty-pr %)) true) ] }
  [ :div
   [ :h1 { :style { :text-align "center" }} "---- " @(re-frame/subscribe [ :game-name ]) " ----" ]
   [ command-page/command-entering ]
   [ board ]  ; Could call (board) also directly, but this "react-way" improves performance if only part of content change
   [ players-table ]
   [ players-list ]        ] )
