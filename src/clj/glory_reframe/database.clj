(ns glory-reframe.database
  (:import (com.google.cloud.datastore DatastoreOptions Entity)))

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

(defonce datastore-options (DatastoreOptions/getDefaultInstance))

(defonce datastore (.getService datastore-options))

(def new-key-factory (-> datastore (.newKeyFactory)))

(def game-key-factory (-> new-key-factory (.setKind "glory-of-empires-game")))

(defn get-game-entity [ game-id ]
  (-> datastore (.get (.newKey game-key-factory game-id))))

(defn get-game [ game-id ]
  (-> game-id
      get-game-entity
      (.getString "game-state")
      (read-string)))

(defn get-sandbox-game [] (get-game 5629499534213120) )

(defn create-game-entity [game]
  (-> (.newKey game-key-factory)
      (Entity/newBuilder)
      (.set "game-state" (str game))
      (.build)))

(defn save-new-game [ game ]
  (-> datastore (.put (create-game-entity game))))

; Updating is based on making a new builder based on copy of existing Entity (which is immutable! :-)
(defn updated-game [ game-state id ]
  (-> (get-game-entity id)
      (Entity/newBuilder)
      (.set "game-state" (str game-state))
      (.build)))

(defn update-game [ game-state id ]
  (-> datastore (.put (updated-game game-state id))))


; datastore.delete(taskKey);