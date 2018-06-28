(ns user
  (:require [glory-reframe.application]
            [com.stuartsierra.component :as component]
            [figwheel-sidecar.config :as fw-config]
            [figwheel-sidecar.system :as fw-sys]
            [reloaded.repl :refer [system init]]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.file :refer [wrap-file]]
            [system.components.middleware :refer [new-middleware]]
            [figwheel-sidecar.repl-api :as figwheel]
            [glory-reframe.components.shell-component :refer [shell-component]]
            [glory-reframe.config :refer [config]]
            [clojure.spec.test.alpha :as stest]))

(defn dev-system []
  (let [config (config)]
    (assoc (glory-reframe.application/app-system config)
           :middleware (new-middleware
                        {:middleware (into [[wrap-file "dev-target/public"]]
                                           (:middleware config))})
           :figwheel-system (fw-sys/figwheel-system (fw-config/fetch-config))
           :css-watcher (fw-sys/css-watcher {:watch-paths ["resources/public/css"]})
    :less (shell-component "lein" "less" "auto"))))

(reloaded.repl/set-init! #(dev-system))

(defn cljs-repl []
  (fw-sys/cljs-repl (:figwheel-system system)))

;; Set up aliases so they don't accidentally
;; get scrubbed from the namespace declaration
(def start reloaded.repl/start)
(def stop reloaded.repl/stop)
(def go reloaded.repl/go)
(def reset reloaded.repl/reset)
(def reset-all reloaded.repl/reset-all)

(stest/instrument)
