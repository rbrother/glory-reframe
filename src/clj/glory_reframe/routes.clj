(ns glory-reframe.routes
  (:require [clojure.java.io :as io]
            [compojure.core :refer [ANY GET PUT POST DELETE routes]]
            [compojure.route :refer [resources]]
            [ring.util.response :refer [response]]
            [taoensso.sente :as sente]
            [taoensso.sente.server-adapters.http-kit :refer [get-sch-adapter]]  ))

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

; Sente Websocket IO
(defn message-received! [{:keys [id client-id ?data]}]
  (println "Server handling message from client: " id client-id ?data)  )

(defn send-to-one-client [client-id message-keyword data]
  (chsk-send! client-id [message-keyword data]))

(defn send-to-all [message-keyword data]
  (doseq [client-id (:any @connected-uids)]
    (send-to-one-client client-id message-keyword data)))

; TODO: Integrate these to compojure

(def router-stop-fn (atom nil))

(defn stop-router! []
  (let [stop-fn @router-stop-fn]
    (when stop-fn (stop-fn))))

(defn start-router! []
  (reset! router-stop-fn
          (sente/start-chsk-router! ch-chsk message-received!)))
