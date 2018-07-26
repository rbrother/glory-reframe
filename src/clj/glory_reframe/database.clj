(ns glory-reframe.database
  (:import (com.google.cloud.datastore DatastoreOptions Entity)))

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

(defn create-key [ kind id ]
  (-> datastore
      (.newKeyFactory)
      (.setKind kind)
      (.newKey id) ))

(defn create-game-key [ id ] (create-key "glory-of-empires-game" id))

(defn get-game [ game-id ]
  (-> datastore
      (.get (create-game-key game-id))
      (.getString "game-state")
      (read-string)))

(defn get-sandbox-game [] (get-game 5629499534213120) )

(defn create-game-entity [game]
  (-> (create-game-key 5849584954894958)
      Entity/newBuilder
      (.set "game-state" (str game))
      (.build)))

(defn save-new-game [ game ]
  (-> datastore
      (.put (create-game-entity game))))
