
(ns app.util (:require ["three" :as Three]))

(defn get-env! [property] (aget (.-env js/process) property))

(defn show-size! [object]
  (let [box (.setFromObject (Three/Box3.) object)]
    (.log js/console "Show size:" (.-min box) (.-max box) (.getSize box (Three/Vector3.)))))
