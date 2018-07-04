(ns glory-reframe.events
  (:require [re-frame.core :as re-frame]
            [glory-reframe.db :as db]
            [glory-reframe.commands :as commands]))

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

(re-frame/reg-event-db
  :execute-command
  (fn [ { command :command-to-execute game-state :game-state :as db } [ _ _ ] ]
    (println "Executing command: " command)
    (let [ [ command-id & pars ] (cljs.reader/read-string (str "[" command "]"))
          command-func-getter (case (keyword command-id)
                         :move commands/move
                         :new commands/new
                         :del commands/del)
          command-func (apply command-func-getter pars)
          role :game-master]
      (assoc db :game-state (command-func game-state role))    )))
