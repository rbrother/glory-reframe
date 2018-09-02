(ns glory-reframe.database
  (:require [com.stuartsierra.component :as component])
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

(defn create-connection []
  (let [ datastore-options (DatastoreOptions/getDefaultInstance) ]
    (println "Creating Google Datastore connection")
    (.getService datastore-options)))

(defn new-key-factory [ db-connection ] (-> db-connection (.newKeyFactory)))

(defn ds-get [ db-connection key] (-> db-connection (.get key)))

(defn ds-put [ db-connection entity] (-> db-connection (.put entity)))

; String can have Up to 1,500 bytes if property is indexed, up to 1 MB otherwise.
(defn create-text [str]
  (-> (StringValue/newBuilder str)
      (.setExcludeFromIndexes true)
      (.build)))

(defn set-attr [ entity-builder [ key val ]] (.set entity-builder (name key) val))

(defn build-entity [ source-key-or-entity attrs-map ]
  (let [ builder (Entity/newBuilder source-key-or-entity) ]
    (.build (reduce set-attr builder attrs-map))    ))

; Stuartsierra Component

(defrecord DatastoreComponent [ db-connection ]
  component/Lifecycle
  (start [component]
    (assoc component :db-connection (create-connection)))
  (stop [component]
    (println "Stopping database connection (no op)")
    (dissoc component :db-connection)  ))

(defn new-datastore []
  (map->DatastoreComponent {}))

; Game-specific stuff

(defn game-key-factory [ db-connection ]
  (-> (new-key-factory db-connection) (.setKind "glory-of-empires-game")))

(defn get-game-entity [ db-connection game-id ]
  (ds-get db-connection (.newKey (game-key-factory db-connection) game-id)))

(defn get-game [ { db-connection :db-connection } game-id ]
  (-> (get-game-entity db-connection game-id)
      (.getString "game-state")
      (read-string)))

(defn get-sandbox-game [ datastore ] (get-game datastore 5629499534213120) )

(defn save-new-game [ { db-connection :db-connection } game]
  (let [ key (.newKey (game-key-factory db-connection))
         entity (build-entity key {:game-state (create-text (str game))}) ]
    (ds-put db-connection entity)   ))

(defn save-game [ { db-connection :db-connection } { game-id :database-id :as game-data} ]
  (let [ key (.newKey (game-key-factory db-connection) game-id)
         entity (build-entity key {:game-state (create-text (str game-data))}) ]
    (ds-put db-connection entity)   ))

; datastore.delete(taskKey);

; GQL query for getting all games: SELECT * FROM `glory-of-empires-game`