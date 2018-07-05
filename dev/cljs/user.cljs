(ns cljs.user
  (:require [glory-reframe.core]
            [glory-reframe.system :as system]
            [clojure.spec.test.alpha :as stest]
            [clojure.spec.alpha :as spec]))

(def go system/go)
(def reset system/reset)
(def stop system/stop)
(def start system/start)

; This location recommented for turning on spec in
; https://stackoverflow.com/questions/45501284/how-to-turn-on-cljs-spec
(stest/instrument)
(spec/check-asserts true)

(println "Executing cljs.user")