(ns glory-reframe.routes
  (:require [clojure.java.io :as io]
            [compojure.core :refer [ANY GET PUT POST DELETE routes]]
            [compojure.route :refer [resources]]
            [ring.util.response :refer [response]]
            [taoensso.sente :as sente]
            [taoensso.sente.server-adapters.http-kit :refer [get-sch-adapter]]
            [glory-reframe.state :as state]
            [glory-reframe.utils :as utils]))

(defn get-client-id [req] (get-in req [:params :client-id]))

(def sente-socket
  (sente/make-channel-socket! (get-sch-adapter) {:user-id-fn get-client-id})   )

(def ring-ajax-post (:ajax-post-fn sente-socket))           ; for receiving ajax post
(def ring-ajax-get-or-ws-handshake (:ajax-get-or-ws-handshake-fn sente-socket))
(def ch-chsk (:ch-recv sente-socket))                       ; for receiving messages
(def chsk-send! (:send-fn sente-socket))                    ; for sending messages
(def connected-uids (:connected-uids sente-socket))         ; read-only atom

(defn raw-routes [endpoint]
  (routes
    (GET "/" _
      (-> "public/index.html"
          io/resource
          io/input-stream
          response
          (assoc :headers {"Content-Type" "text/html; charset=utf-8"})))
    (GET  "/ws" req (ring-ajax-get-or-ws-handshake req))     ; websocket
    (POST "/ws" req (ring-ajax-post                req))     ; websocket
    (resources "/")))

(defn home-routes [endpoint]
  (-> (raw-routes endpoint)
      ring.middleware.keyword-params/wrap-keyword-params
      ring.middleware.params/wrap-params))

(defn send-to-one-client [client-id message-keyword data]
  (chsk-send! client-id [message-keyword data]))

(defn send-to-all [message-keyword data]
  (doseq [client-id (:any @connected-uids)]
    (send-to-one-client client-id message-keyword data)))

; Move to state?
(defonce client-info (atom {}))                             ; { <client-id> { :game 1234 } ... }

(defn load-game [client-id game-id]
  (let [game (state/get-game game-id)]
    (swap! client-info #(assoc-in % [client-id :game ] game-id))
    (send-to-one-client client-id :glory-reframe.websocket/game-loaded game)))

(defn clients-to-notify [ clients-info sender-id game-id ]
  {:post [(do (println "clients-to-notify: " (utils/pretty-pr %)) true)]}
  (->> clients-info
       (filter (fn [ [ client {game :game} ] ]
                 (and (= game game-id) (not= client sender-id))))
       (map first)  ))

(defn update-game [client-id { game-id :database-id :as game-data}]
  ; First notify other clients (for fast response), then save to database
  (doseq [client-id (clients-to-notify @client-info client-id game-id)]
    (send-to-one-client client-id :glory-reframe.websocket/game-modified game-data))
  (state/set-game game-data))

; Sente Websocket IO
(defn message-received! [{:keys [id client-id ?data]}]
  (println "Server handling client message: " id client-id ?data)
  (case id
    :chsk/ws-ping nil
    :chsk/uidport-open (swap! client-info #(assoc-in % [?data :connected ] true))
    :chsk/uidport-close (swap! client-info #(dissoc % ?data))
    :glory-reframe.websocket/load-game (load-game client-id ?data)
    :glory-reframe.websocket/update-game (update-game client-id ?data)
    (println "Server does not know how to handle: " id ?data)  )
  (println @client-info))

; TODO: Integrate these to compojure, now hacked to (go) func.
; Current hack does not work well with automatic reload, needing restart

(def router-stop-fn (atom nil))

(defn stop-router! []
  (println "SERVER: stop-router!")
  (let [stop-fn @router-stop-fn]
    (when stop-fn (stop-fn))))

(defn start-router! []
  (println "SERVER: start-router!")
  (reset! router-stop-fn
          (sente/start-chsk-router! ch-chsk message-received!)))
