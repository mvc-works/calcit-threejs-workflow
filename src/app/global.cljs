
(ns app.global (:require ["three" :as Three]))

(defonce *camera-info
  (atom {:position (Three/Vector3. 0 0 100), :orientation (Three/Vector3. 0 0 -1)}))

(defonce *mouse-states (atom {:dragging? false, :p0 {:x 0, :y 0}}))

(defonce *objects (atom {}))

(defonce *values (atom {:time 0}))

(defonce scene (new Three/Scene))

(defn add-object! [pairs]
  (assert (map? pairs) "use map for adding objects...")
  (doseq [[k obj] pairs] (comment .log js/console "add object" k obj) (.add scene obj))
  (swap! *objects merge pairs)
  (println (keys @*objects)))

(defonce camera
  (Three/PerspectiveCamera.
   50
   (/ (.-innerWidth js/window) (.-innerHeight js/window))
   0.1
   1000))

(defonce renderer (new Three/WebGLRenderer))
