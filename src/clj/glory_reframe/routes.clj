(ns glory-reframe.routes
  (:require [clojure.java.io :as io]
            [compojure.core :refer [ANY GET PUT POST DELETE routes]]
            [compojure.route :refer [resources]]
            [ring.util.response :refer [response]]
            [taoensso.sente :as sente]
            [taoensso.sente.server-adapters.http-kit :refer [get-sch-adapter]]
            [glory-reframe.state :as state]
            [glory-reframe.utils :as utils]
            [com.stuartsierra.component :as component]))

(defn get-client-id [req] (get-in req [:params :client-id]))

(defn raw-routes [ { get-func :ring-ajax-get-or-ws-handshake post-func :ring-ajax-post :as sente }  ]
  (routes
    (GET "/" _
      (-> "public/index.html"
          io/resource
          io/input-stream
          response
          (assoc :headers {"Content-Type" "text/html; charset=utf-8"})))
    (GET  "/ws" req (get-func req))     ; websocket
    (POST "/ws" req (post-func req))     ; websocket
    (resources "/")))

(defn home-routes [ endpoint-component ]
  (-> (raw-routes (:sente endpoint-component) )
      ring.middleware.keyword-params/wrap-keyword-params
      ring.middleware.params/wrap-params))

(defn send-to-all [ chsk-send! connected-uids message-keyword data ]
  (doseq [client-id (:any connected-uids)]
    (chsk-send! client-id [message-keyword data])))

(defn load-game [ chsk-send! client-info database client-id game-id]
  (let [game (state/get-game database game-id)]
    (swap! client-info #(assoc-in % [client-id :game ] game-id))
    (chsk-send! client-id [ :glory-reframe.websocket/game-loaded game ] )   ))

(defn clients-to-notify [ clients-info sender-id game-id ]
  {:post [(do (println "clients-to-notify: " (utils/pretty-pr %)) true)]}
  (->> clients-info
       (filter (fn [ [ client {game :game} ] ]
                 (and (= game game-id) (not= client sender-id))))
       (map first)  ))

(defn update-game [ chsk-send! client-info database client-id { game-id :database-id :as game-data}]
  ; First notify other clients (for fast response), then save to database
  (doseq [client-id (clients-to-notify @client-info client-id game-id)]
    (chsk-send! client-id [ :glory-reframe.websocket/game-modified game-data ] ))
  (state/set-game database game-data))

; Sente Websocket IO
(defn message-received! [ chsk-send! client-info database {:keys [id client-id ?data]}]
  (println "Server handling client message: " id client-id ?data)
  (case id
    :chsk/ws-ping nil
    :chsk/uidport-open (swap! client-info #(assoc-in % [?data :connected ] true))
    :chsk/uidport-close (swap! client-info #(dissoc % ?data))
    :glory-reframe.websocket/load-game (load-game chsk-send! client-info database client-id ?data)
    :glory-reframe.websocket/update-game (update-game chsk-send! client-info database client-id ?data)
    (println "Server does not know how to handle: " id ?data)  )
  (println @client-info))

(defrecord SenteComponent [ client-info router-stop-fn ring-ajax-post ring-ajax-get-or-ws-handshake ]
  component/Lifecycle
  (start [ { database :database :as component }  ]
    (println "SERVER: start-router!")
    (let [ sente-socket (sente/make-channel-socket! (get-sch-adapter) {:user-id-fn get-client-id})
           ch-chsk (:ch-recv sente-socket)
           chsk-send! (:send-fn sente-socket)
           client-info (atom {}) ; { <client-id> { :game 1234 } ... }
           sente-message-received (partial message-received! chsk-send! client-info database) ]
      (assoc component :client-info client-info
                       :router-stop-fn (sente/start-chsk-router! ch-chsk sente-message-received)
                       :ring-ajax-post (:ajax-post-fn sente-socket)
                       :ring-ajax-get-or-ws-handshake (:ajax-get-or-ws-handshake-fn sente-socket) )))
  (stop [ { router-stop-fn :router-stop-fn :as component } ]
    (println "SERVER: stop-router!" )
    (router-stop-fn)
    (dissoc component :router-stop-fn :client-info :ring-ajax-post
            :ring-ajax-get-or-ws-handshake :ch-chsk )))

(defn new-sente-component [] (map->SenteComponent {}))
