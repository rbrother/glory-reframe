(ns glory-reframe.events
  (:require [re-frame.core :as re-frame]
            [glory-reframe.db :as db]
            [glory-reframe.commands :as commands]
            [clojure.spec.alpha :as spec]
            [clojure.spec.test.alpha :as stest]))

; Safe modification version with SPEC
(defn- modify-db [ db func ]
  ; Unfortunately it is *as designed* that spec/instrument only checks
  ; args of functions and NOT :ret return value!
  ; https://groups.google.com/forum/#!msg/clojure/jcVnjk1MOWY/UwP5bc1oCAAJ
  ; So Rich recommends :post test instead (spec/assert) is turned on only in dev
  {:post [ (spec/assert :glory-reframe.map/game-state %) ] }
  (func db))

(spec/fdef modify-db
           :args (spec/cat :game-state :glory-reframe.map/game-state :func fn?)
           :ret :glory-reframe.map/game-state)

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

(defn- get-command-fn [ command-id ]
  { :post [ (spec/assert fn? %) ] }
  (let [command-getters (ns-publics 'glory-reframe.commands)
        command-getter (command-getters command-id)]
    (or command-getter (throw (str "Command function not found: " command-id)))))

(re-frame/reg-event-db
  :execute-command
  (fn [ { command :command-to-execute game-state :game-state :as db } [ _ _ ] ]
    (println "Executing command: " command)
     (let [[command-id & pars] (cljs.reader/read-string (str "[" command "]"))
           command-func (apply (get-command-fn command-id) pars)
           role :game-master]
       (assoc db :game-state (modify-db game-state #(command-func % role))    ))))
