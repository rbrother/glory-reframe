(ns glory-reframe.views
  (:require [re-frame.core :as re-frame]
            [glory-reframe.views.map-svg :as map-svg]
            [glory-reframe.views.players :as players]))

(defn board
  ( [ ] (board {}) )
  ( [ opts ]
    (if (number? opts) (board { :scale opts })
      ; TODO: Reduce dependencies to only the relevant parts of game-state to prevent unnecessary updates
      (let [ game @(re-frame/subscribe [:game]) ]
        (map-svg/render-map game opts)    ))))

(defn players-html []
  (let [ game @(re-frame/subscribe [:game]) ]
    (players/players-html game :admin)))

(defn main-panel []
  [:div
   [board]  ; Could call (board) also directly, but this "react-way" improves performance if only part of content change
   [players-html]])
