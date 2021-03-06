(ns glory-reframe.websocket
  (:require [taoensso.sente :as sente]
            [re-frame.core :refer [dispatch]]))

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
  (case key
    :glory-reframe.websocket/game-loaded (dispatch [:game-loaded message])
    :glory-reframe.websocket/game-modified (dispatch [:game-modified message])
    (println "Client unhandled message from server: " key message)))

(def router (atom nil))

(defn stop-router! []
  (println "Client stopping Sente websocket-router")
  (when-let [stop-f @router] (stop-f)))

(defn start-router! []
  (stop-router!)
  (println "Client starting Sente websocket-router")
  (reset! router
    (sente/start-chsk-router!
      ch-chsk
      (event-msg-handler
        { :message message-handler
          :state state-handler
          :handshake handshake-handler }   ))))