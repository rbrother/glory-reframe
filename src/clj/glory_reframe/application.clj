(ns glory-reframe.application
  (:gen-class)
  (:require [com.stuartsierra.component :as component]
            [glory-reframe.components.server-info :refer [server-info]]
            [system.components.endpoint :refer [new-endpoint]]
            [system.components.handler :refer [new-handler]]
            [system.components.middleware :refer [new-middleware]]
            [system.components.http-kit :refer [new-web-server]]
            [glory-reframe.config :refer [config]]
            [glory-reframe.routes :as routes]
            [glory-reframe.database :as db]))

(defn app-system [ { middleware :middleware port :http-port :as config } ]
  (component/system-map
    :routes (->(new-endpoint routes/home-routes) (component/using [:sente]))
    :middleware (new-middleware { :middleware middleware } )
    :handler (-> (new-handler) (component/using [ :routes :middleware ]))
    :http (-> (new-web-server port) (component/using [:handler]))
    :server-info (server-info port)
    ; Added components
    :database (db/new-datastore)
    :sente (-> (routes/new-sente-component)
               (component/using [:database]))    ))

(defn -main [& _]
  (let [config (config)]
    (component/start (app-system config))))
