(ns glory-reframe.views
  (:require [re-frame.core :as re-frame]
            [glory-reframe.views.map-svg :as map-svg]
            [glory-reframe.views.players :as players]
            [glory-reframe.views.command-page :as command-page]
            [glory-reframe.utils :as utils]
            [glory-reframe.views.svg :as svg]))

(defn board-background []
  (let [ board* @(re-frame/subscribe [:board] )]
    (map-svg/render-map-background board*)   ))

(defn board-units []
  (let [ board* @(re-frame/subscribe [:board])
         planets @(re-frame/subscribe [:planets])
         units @(re-frame/subscribe [:units])]
    (map-svg/render-map-pieces board* planets units)    ))

(defn board
  ( [ ] (board {}) )
  ( [ opts ]
   #_{:post [ (do (println (utils/pretty-pr %)) true) ] }
   (println "rendering: board")
    (if (number? opts) (board { :scale opts })
      ; TODO: Render separately (first) board and then pieces
      ; TODO: Make each tile in the pieces-map separate component for
                       ;                better perforemance. No need for
                       ; board-tiles since they change rarely. Unless
                       ; when building board.
      (let [ board* @(re-frame/subscribe [:board])
            map-pieces (vals board*)
            scale (or (:scale opts) 0.5)
            [ min-corner max-corner :as bounds] (glory-reframe.map/bounding-rect map-pieces)
            svg-size (utils/mul-vec (utils/rect-size bounds) scale) ]
        (svg/svg svg-size
           (svg/g { :scale scale :translate (utils/mul-vec min-corner -1.0) }
                  [ [ board-background ]
                    [ board-units] ]
                  ))))))

(defn players-table [ ]
  (println "rendering: players-table")
  (let [ players @(re-frame/subscribe [:players]) ]
    (players/players-table players) ))

(defn players-list []
  (println "rendering: players-list")
  (let [ players @(re-frame/subscribe [:players] )
        role :admin ]
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
