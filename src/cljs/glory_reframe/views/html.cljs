(ns glory-reframe.views.html)

#_(def resources-url "http://www.brotherus.net/ti3/")       ; in production
(def resources-url "http://192.168.10.10/ti3/")             ; local dev

(defn select [ attrs options ]
  (into [:select attrs] (map (fn [opt] [:option {} opt]) options)))

(defn- make-col [ col ]
  (if (and (coll? col) (map? (first col)))
     [ :td (first col) (rest col) ]
     [ :td col ] ))

(defn td-items [ items ] (map (fn [i] [ :td i ]) items))

(defn- make-row-inner [ attrs cells ] [ :tr attrs (map make-col cells) ] )

(defn- make-row [ row ]
  (if (map? (first row))
    (make-row-inner (first row) (rest row))
    (make-row-inner {} row)))

(defn table [ attrs rows ] [ :table attrs (into [:tbody] (map make-row rows))] )

(defn ul [ items ] (into [:ul] (map (fn [item] [:li item]) items)))

(defn ol [ items ] (into [:ol] (map (fn [item] [:li item]) items)))

(defn color-span [ color content ] [ :span { :style { :color color :font-weight "bold" } } content ] )
