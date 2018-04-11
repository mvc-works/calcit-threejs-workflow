
(ns app.global (:require ["three" :as Three]))

(defonce *objects (atom {}))

(defonce *values (atom {:time 0}))

(defonce scene (new Three/Scene))

(defn add-scene-object! [pairs]
  (doseq [[k obj] pairs] (.add scene obj))
  (swap! *objects merge pairs))

(defonce camera
  (Three/PerspectiveCamera.
   50
   (/ (.-innerWidth js/window) (.-innerHeight js/window))
   0.1
   1000))

(defonce renderer (new Three/WebGLRenderer))
