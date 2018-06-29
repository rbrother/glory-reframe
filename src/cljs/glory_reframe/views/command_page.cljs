(ns glory-reframe.views.command-page
  (:require [glory-reframe.views.html :as html]
           [glory-reframe.utils :as utils]))

(def command-examples-select
  (html/select { :id "examples" :onchange "ExampleChanged();"}
               [ "---- game setup ----"
                "round-board 3"
                "rect-board 10 5"
                "set-systems-random"
                "set-system c4 mecatol-rex"
                "del-system c4"
                "set-players hacan \"abc\" norr \"xyz\" naalu \"123\""
                "---- unit operations ----"
                "activate d3"
                "new hacan ca b2"
                "new norr ca cr 2 fi a2"
                "new hacan 2 gf meer"
                "new 2 gf meer"
                "del ca3"
                "del hacan from c1 gf"
                "del from c1 2 gf ca"
                "move ca1 a2"
                "move ca1 ca2 b1"
                "move from d1 ws2 ca 3 gf b1"
                "move from d1 ws2 ca 3 gf b1"
                "move from d1 all b1"
                "---- cards ----"
                "ac-deck-create"
                "ac-get"
                "ac-get hacan"
                "ac-play local-unrest \"details...\""]))

(defn command-entering [ ]
  #_{:post [(do (println (utils/pretty-pr %)) true)]}
  [ :table
    [ :tbody
      [ :tr
       [ :td "Command" ]
       [ :td [:input {:style {:width "300px"} :id "command" :autofocus "true"}] ]
       [ :td [:button {:type "button" } "Execute"] ]
       [ :td "Examples" ]
       [ :td command-examples-select ] ]
      [ :tr
       [ :td " " ]
       [ :td {:colSpan 4}
        [:span {:id "currentCommand" :style { :color "green" } }] " - "
        [:span {:id "commandResult"}]   ]]]])

