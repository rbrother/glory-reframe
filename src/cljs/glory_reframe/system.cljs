(ns glory-reframe.system
  (:require [com.stuartsierra.component :as component]
            [glory-reframe.components.ui :refer [new-ui-component]]
            [glory-reframe.websocket :as ws]))

(declare system)

(defn new-system []
  (component/system-map
   :app-root (new-ui-component)))

(defn init []
  (set! system (new-system)))

(defn start []
  (ws/start-router!)
  (set! system (component/start system)))

(defn stop []
  (ws/stop-router!)
  (set! system (component/stop system)))

(defn ^:export go []
  (init)
  (start))

(defn reset []
  (stop)
  (go))
