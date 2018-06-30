(ns glory-reframe.subs
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [re-frame.core :as re-frame]
            [glory-reframe.strategies :as strat]
            [glory-reframe.systems :as systems]
            [glory-reframe.utils :as utils]))

(re-frame/reg-sub :game-name (fn [db] (:name db)))

(re-frame/reg-sub :command-to-execute (fn [db] (:command-to-execute db)))

(re-frame/reg-sub :board (fn [ { { board :map} :game-state } ] board))

(re-frame/reg-sub :players-raw (fn [ { { players :players} :game-state } ] players))

(re-frame/reg-sub :units (fn [ { { units :units } :game-state } ] units))

(re-frame/reg-sub :strategies-raw (fn [ { { strategies :strategies } :game-state } ] strategies))

; Strategies with amended properties
(re-frame/reg-sub :strategies
  :<- [ :strategies-raw ]
  (fn [ strategies-raw _ ]
    (sort-by :order (clojure.set/join strategies-raw strat/all-strategies-arr)    )))

(re-frame/reg-sub :planets-raw
                  (fn [ { { planets :glory-reframe.map/planets } :game-state } ] planets))

; Planets with amended properties
(re-frame/reg-sub :planets
  :<- [ :planets-raw ]
  (fn [ planets-raw _ ]
    (clojure.set/join (vals planets-raw) systems/all-planets-set)))

(defn- player-controls [race-id]
  (fn [ { controller :controller owner :owner } ]
    (or (= controller race-id) (= owner race-id) )))

(defn- filter-player [ race-id items ] (filter (player-controls race-id) items))

(defn- amend-player [ { player-id :id :as player } strategies planets board ]
  (assoc player :strategies (filter-player player-id strategies)
                :planets (filter-player player-id planets)
                :systems (filter-player player-id board)  ))

(re-frame/reg-sub :players
  :<- [ :players-raw ]
  :<- [ :strategies ]
  :<- [ :planets ]
  :<- [ :board ]
  (fn [ [ players-raw strategies planets board ] _ ]
    (let [ player-order (->> strategies (map :owner) (filter identity) distinct) ]
      (->> player-order
           (map #(players-raw %),)
           (map #(amend-player % strategies planets board)))     )))

