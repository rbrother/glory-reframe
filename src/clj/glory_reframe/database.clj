(ns glory-reframe.database
  (:import (com.google.cloud.datastore DatastoreOptions Entity StringValue)))

; Javadoc: https://googlecloudplatform.github.io/google-cloud-java/google-cloud-clients/apidocs/index.html
; Higher level API docs: https://cloud.google.com/datastore/docs/concepts/entities

; Based on https://cloud.google.com/datastore/docs/reference/libraries
; Requires that was have set environment variable
; GOOGLE_APPLICATION_CREDENTIALS=[PATH].json to the private-key json-file
; of the service-account for the datastore project

; To do some repl-development in this (or similar) namespace, try switching
; to the namespace in repl with in-ns:
; user=> (require 'glory-reframe.database)
; user=> (in-ns 'glory-reframe.database)
; glory-reframe.database=> datastore
; #object[clojure.lang.Namespace 0x54b7e0f0 "glory-reframe.database"]

; General funcs for google datastore

(defonce datastore (atom nil))

(defn create-connection []
  (let [ datastore-options (DatastoreOptions/getDefaultInstance) ]
    (reset! datastore (.getService datastore-options))
    (println "Database connection made!")))

(defn new-key-factory [] (-> @datastore (.newKeyFactory)))

(defn ds-get [key] (-> @datastore (.get key)))

(defn ds-put [entity] (-> @datastore (.put entity)))

; String can have Up to 1,500 bytes if property is indexed, up to 1 MB otherwise.
(defn create-text [str]
  (-> (StringValue/newBuilder str)
      (.setExcludeFromIndexes true)
      (.build)))

(defn set-attr [entity-builder [ key val ]] (.set entity-builder (name key) val))

(defn build-entity [ source-key-or-entity attrs-map ]
  (let [ builder (Entity/newBuilder source-key-or-entity) ]
    (.build (reduce set-attr builder attrs-map))    ))

; Game-specific stuff

(defn game-key-factory [] (-> (new-key-factory) (.setKind "glory-of-empires-game")))

(defn get-game-entity [ game-id ] (ds-get (.newKey (game-key-factory) game-id)))

(defn get-game [ game-id ]
  (-> game-id
      get-game-entity
      (.getString "game-state")
      (read-string)))

(defn get-sandbox-game [] (get-game 5629499534213120) )

(defn save-new-game [game]
  (ds-put (build-entity (.newKey (game-key-factory)) {:game-state (create-text (str game))})))

; Updating is based on making a new builder based on copy of existing Entity (which is immutable! :-)
(defn update-game [ game-state id ]
  (ds-put (build-entity (get-game-entity id) {:game-state (create-text (str game-state))})))

; datastore.delete(taskKey);