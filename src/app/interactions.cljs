
(ns app.interactions
  (:require [app.global :refer [*mouse-states]] [app.global :as global] ["three" :as Three]))

(defn on-mousedown! [event]
  (swap! *mouse-states assoc :dragging? true)
  (swap! *mouse-states assoc :p0 {:x (.-clientX event), :y (.-clientY event)}))

(defn on-mousemove! [event]
  (let [point {:x (.-clientX event), :y (.-clientY event)}
        previous-point (:p0 @*mouse-states)]
    (when (:dragging? @*mouse-states)
      (let [dx (- (:x point) (:x previous-point)), dy (- (:y point) (:y previous-point))]
        (println "x:" dx " y:" dy))
      (swap! *mouse-states assoc :p0 point))))

(defn on-mouseup! [event] (swap! *mouse-states assoc :dragging? false))

(defn on-wheel! [event]
  (.preventDefault event)
  (let [dx (.-deltaX event)
        dy (.-deltaY event)
        orientation (:orientation @global/*camera-info)
        factor (* 0.1 (- dy))]
    (swap!
     global/*camera-info
     update
     :position
     (fn [position]
       (Three/Vector3.
        (+ (.-x position) (* factor (.-x orientation)))
        (+ (.-t position) (* factor (.-y orientation)))
        (+ (.-z position) (* factor (.-z orientation))))))))
