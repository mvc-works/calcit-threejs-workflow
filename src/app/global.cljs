
(ns app.global (:require ["three" :as Three]))

(defonce *mouse-states (atom {:dragging? false, :p0 {:x 0, :y 0}}))

(defonce *objects (atom {}))

(defonce *values (atom {:time 0}))

(defonce scene (new Three/Scene))

(defn add-object! [pairs]
  (doseq [[k obj] pairs] (comment .log js/console "add object" k obj) (.add scene obj))
  (swap! *objects merge pairs))

(defonce camera
  (Three/PerspectiveCamera.
   50
   (/ (.-innerWidth js/window) (.-innerHeight js/window))
   0.1
   1000))

(defonce renderer (new Three/WebGLRenderer))
