(ns glory-reframe.events
  (:require [re-frame.core :as re-frame]
            [glory-reframe.db :as db]))

(re-frame/reg-event-db
 :initialize-db
 (fn  [_ _]
   db/default-db))

(re-frame/reg-event-db
  :select-command-example
  (fn [db [ _ command ] ]
    (assoc db :command-to-execute command)))

(re-frame/reg-event-db
  :command-to-execute-changed
  (fn [db [ _ command ] ]
    (assoc db :command-to-execute command)))
