
(ns app.util (:require ["three" :as Three]))

(defn show-size! [object]
  (let [box (.setFromObject (Three/Box3.) object)]
    (.log js/console "Show size:" (.-min box) (.-max box) (.size box))))
