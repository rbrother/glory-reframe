(ns glory-reframe.subs
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [re-frame.core :as re-frame]
            [glory-reframe.strategies :as strat]
            [glory-reframe.systems :as systems]))

(re-frame/reg-sub :game-name (fn [db] (:name db)))

(re-frame/reg-sub :game (fn [db] (-> db :game-state )))

(re-frame/reg-sub :board (fn [ { { board :map} :game-state } ] board))

; Strategies with amended properties
(re-frame/reg-sub :strategies
  (fn [ { { strategies :strategies} :game-state } ]
    (sort-by :order (clojure.set/join strategies strat/all-strategies-arr)    )))

; Planets with amended properties
(re-frame/reg-sub :planets
  (fn [ { { planets :planets} :game-state } ]
    (clojure.set/join (vals planets) systems/all-planets-set)))

(defn- player-controls [race-id]
  (fn [ { controller :controller owner :owner } ]
    (or (= controller race-id) (= owner race-id) )))

(defn- filter-player [ race-id items ] (filter (player-controls race-id) items))

(defn- amend-player [ { player-id :id :as player } strategies planets board ]
  (assoc player :strategies (filter-player player-id strategies)
                :planets (filter-player player-id planets)
                :systems (filter-player player-id board)  ))

(re-frame/reg-sub :players
  (fn [ { { players :players} :game-state } ]
    (let [ strategies @(re-frame/subscribe [:strategies])
          planets @(re-frame/subscribe [:planets])
          board @(re-frame/subscribe [:board])
          player-order (->> strategies (map :owner) (filter identity) distinct) ]
      (->> player-order (map #(players %)) (map #(amend-player % strategies planets board)))     )))

(re-frame/reg-sub :units
  (fn [ { { units :units} :game-state } ] units ))
