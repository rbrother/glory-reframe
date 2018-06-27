(ns glory-reframe.subs
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [re-frame.core :as re-frame]
            [glory-reframe.strategies :as strat]
            [glory-reframe.systems :as systems]))

(re-frame/reg-sub :game-name (fn [db] (:name db)))

(re-frame/reg-sub :game (fn [db] (-> db :game-state )))

; Strategies with amended properties
(re-frame/reg-sub :strategies
  (fn [ { { strategies :strategies} :game-state } ]
    (sort-by :order
             (clojure.set/join strategies strat/all-strategies-arr)    )))

; Planets with amended properties
(re-frame/reg-sub :planets
  (fn [ { { planets :planets} :game-state } ]
    (clojure.set/join (vals planets) systems/all-planets-set)))

(re-frame/reg-sub :players
  (fn [ { { players :players} :game-state } ] players ))

(re-frame/reg-sub :units
  (fn [ { { units :units} :game-state } ] units ))
