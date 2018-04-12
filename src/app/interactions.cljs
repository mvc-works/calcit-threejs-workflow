
(ns app.interactions (:require [app.global :refer [*mouse-states]]))

(defn on-mousedown! [event]
  (swap! *mouse-states assoc :dragging? true)
  (swap! *mouse-states assoc :p0 {:x (.-clientX event), :y (.-clientY event)}))

(defn on-mousemove! [event]
  (let [point {:x (.-clientX event), :y (.-clientY event)}
        previous-point (:p0 @*mouse-states)]
    (when (:dragging? @*mouse-states)
      (println "change from" previous-point point)
      (swap! *mouse-states assoc :p0 point)
      (println @*mouse-states)
      (.log js/console event))))

(defn on-mouseup! [event] (swap! *mouse-states assoc :dragging? false))
