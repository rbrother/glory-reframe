(ns glory-reframe.views
  (:require [re-frame.core :as re-frame]
            [glory-reframe.views.map-svg :as map-svg]
            [glory-reframe.views.players :as players]))

(defn board
  ( [ ] (board {}) )
  ( [ opts ]
   (println "rendering: board")
    (if (number? opts) (board { :scale opts })
      ; TODO: Reduce dependencies to only the relevant parts of game-state to prevent unnecessary updates
      (let [ game @(re-frame/subscribe [:game]) ]
        (map-svg/render-map game opts)    ))))

(defn players-html []
  (println "rendering: players-html")
  ; Collect here all subscriptions so that the inner function players/players-html can be pure (and testable)
  (let [ players @(re-frame/subscribe [:players])
        role :admin ]
        (into [:div (players/players-table players)]
              (map (partial players/player-html role) players))))

(defn main-panel []
  [ :div
   [ :h1 { :style { :text-align "center" }} "---- " @(re-frame/subscribe [ :game-name ]) " ----" ]
   [ board ]  ; Could call (board) also directly, but this "react-way" improves performance if only part of content change
   [ players-html ]        ] )
