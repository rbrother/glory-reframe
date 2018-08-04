(ns glory-reframe.websocket
  (:require [taoensso.sente :as sente]))

(let [connection (sente/make-channel-socket! "/ws" {:type :auto})]
  (def ch-chsk (:ch-recv connection))                       ; ChannelSockets receive channel
  (def send-message! (:send-fn connection))   )

(defn state-handler [ {:keys [?data]} ]
  (println "state-handler: " ?data))

(defn handshake-handler [ {:keys [?data]} ]
  (println "connection established: " ?data))

(defn default-event-handler [ev-message]
  (println "unhandled event: " ev-message))

(defn event-msg-handler [ & [ {:keys [message state handshake]
                               :or {state state-handler handshake handshake-handler}}]]
  (fn [ev-msg]
    (case (:id ev-msg)
      :chsk/handshake (handshake ev-msg)
      :chsk/state (state ev-msg)
      :chsk/recv (message ev-msg)
      (default-event-handler ev-msg))))

(defn message-handler [ { [ key message ] :?data } ]
  (println "Client handling message from server: " key message))

(def router (atom nil))

(defn stop-router! []
  (when-let [stop-f @router] (stop-f)))

(defn start-router! []
  (stop-router!)
  (reset! router
          (sente/start-chsk-router!
            ch-chsk
            (event-msg-handler
              { :message message-handler
                :state state-handler
                :handshake handshake-handler }   ))))