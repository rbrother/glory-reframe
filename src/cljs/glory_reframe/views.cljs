(ns glory-reframe.views
  (:require [re-frame.core :as re-frame]
            [glory-reframe.map-svg :as map-svg]))

(defn board
  ( [ ] (board {}) )
  ( [ opts ]
    (if (number? opts) (board { :scale opts })
      ; TODO: Reduce dependencies to only the relevant parts of game-state to prevent unnecessary updates
      (let [ board @(re-frame/subscribe [:game]) ]
        (map-svg/render game opts)    ))))

(defn main-panel []
  (let [name (re-frame/subscribe [:name])]
    (fn []
      (board))))
