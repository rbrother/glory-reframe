(ns glory-reframe.state
  (:require [glory-reframe.database :as db]))

(defonce server-state (atom {}))           ; TODO: Make component, make spec shared, use here

(defn get-game [ datastore id]
  (if-let [ cached-game (get-in @server-state [ :games id ])]
    cached-game
    (if-let [game (db/get-game datastore id)]
      (do
        (swap! server-state assoc-in [:games id] game)
        (println "Cached games: " (keys (:games @server-state)))
        game)
      (throw (Exception. "my exception message"))  )))

(defn set-game [ datastore { game-id :database-id :as game-data} ]
  (let [game-swapper (fn [state]
                       (db/save-game datastore game-data)
                       (assoc-in state [:games game-id] game-data))]
    ; We swap with function that includes both saving to db and atom-exchange,
    ; merging both to single atomic operation.
    (swap! server-state game-swapper)))
