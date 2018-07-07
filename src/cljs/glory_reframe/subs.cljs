(ns glory-reframe.subs
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [re-frame.core :as re-frame]
            [glory-reframe.strategies :as strat]
            [glory-reframe.systems :as systems]
            [glory-reframe.utils :as utils]
            [cljs.spec.alpha :as spec]))

(re-frame/reg-sub :game-name (fn [db] (:name db)))

(re-frame/reg-sub :command-to-execute (fn [db] (:command-to-execute db)))

(re-frame/reg-sub :board (fn [ { { board :glory-reframe.map/map} :game-state } ] board))

(re-frame/reg-sub :board-piece-ids
  :<- [ :board ]
  (fn [ board _ ] (keys board))   )

(re-frame/reg-sub :board-piece
  :<- [ :board ]
  (fn [ board [ _ piece-id ] ]
    {:post [(spec/assert :glory-reframe.map/board-piece %)]}
    (board piece-id))   )

(re-frame/reg-sub :players-raw (fn [ { { players :players } :game-state } ] players))

(re-frame/reg-sub :units (fn [ { { units :units } :game-state } ] units))

(re-frame/reg-sub :units-at
  :<- [ :units ]
  (fn [ units [ _ loc-id ] ] (filter #(= loc-id (% :location)) (vals units)))  )

(re-frame/reg-sub :ships-at
  (fn [ [ _ loc-id ] _ ] (re-frame/subscribe [:units-at loc-id]) )
  (fn [ units _ ] (remove :planet units))    )

(re-frame/reg-sub :ground-units-at
  (fn [ [ _ loc-id planet ] _ ] (re-frame/subscribe [:units-at loc-id]) )
  (fn [ units [ _ loc-id planet ] ] (filter #(= planet (% :planet)) units))    )

(re-frame/reg-sub :strategies-raw
  (fn [ { { strategies :strategies } :game-state } ] strategies))

; Strategies with amended properties
(re-frame/reg-sub :strategies
  :<- [ :strategies-raw ]
  (fn [ strategies-raw _ ]
    (sort-by :order (clojure.set/join strategies-raw strat/all-strategies-arr)    )))

(re-frame/reg-sub :planets-raw
                  (fn [ { { planets :glory-reframe.map/planets } :game-state } ] planets))

(defn- amend-planet [ { id :id :as planet } ]
  (merge planet (systems/all-planets-map id)))

; Planet with amended properties
(re-frame/reg-sub :planet
  :<- [ :planets-raw ]
  (fn [ planets-raw [ _ planet-id ] ]
    {:post [ (spec/assert :glory-reframe.map/planet %) ] }
    (amend-planet (planets-raw planet-id)))    )

; Planets with amended properties -> TODO: Get rid of, move to use :planet above
(re-frame/reg-sub :planets
  :<- [ :planets-raw ]
  (fn [ planets-raw _ ]
    (clojure.set/join (vals planets-raw) systems/all-planets-set)))

(defn- player-controls [race-id]
  (fn [ { controller :controller owner :owner } ]
    (or (= controller race-id) (= owner race-id) )))

(defn- filter-player [ race-id items ] (filter (player-controls race-id) items))

(defn- amend-player [ { player-id :glory-reframe.races/id :as player } strategies planets board ]
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
           (map #(players-raw %))
           (map #(amend-player % strategies planets board)))     )))

