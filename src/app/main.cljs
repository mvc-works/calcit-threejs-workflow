
(ns app.main
  (:require [app.schema :as schema] [cljs.reader :refer [read-string]] ["three" :as Three]))

(defonce *global-objects (atom {}))

(defonce global-camera (Three/PerspectiveCamera. 35 (/ 1200 800) 0.1 1000))

(defonce global-renderer (new Three/WebGLRenderer))

(defonce global-scene (new Three/Scene))

(defn add-scene-object! [pairs]
  (doseq [[k obj] pairs] (.add global-scene obj))
  (swap! *global-objects merge pairs)
  (.render global-renderer global-scene global-camera))

(def mount-target (.querySelector js/document ".app"))

(defn render-objects! []
  (.log js/console Three/ObjectLoader)
  (let [geometry (Three/BoxGeometry. 2 2 2)
        material (Three/MeshLambertMaterial. (clj->js {:color 0xff0000}))
        mesh (Three/Mesh. geometry material)
        light (Three/PointLight. 0xffff00)
        loader (Three/ObjectLoader.)]
    (.. light -position (set -5 -5 -5))
    (.. mesh -position (set (rand-int 5) (rand-int 5) (rand-int 5)))
    (add-scene-object! {:mesh mesh, :light light})
    (.load
     loader
     "/entry/teapot.json"
     (fn [teapot] (.log js/console "teapot" teapot) (add-scene-object! {:teapot teapot}))
     (fn [xhr] (println (str (* 100 (/ (.-loaded xhr) (.-total xhr))) "% loaded")))
     (fn [error] (.log js/console error)))
    (.log js/console 1)))

(defn initialize-canvas! []
  (let []
    (.setSize global-renderer 1200 800)
    (.setClearColor global-renderer 0xdddddd 1)
    (.appendChild mount-target (.-domElement global-renderer))
    (.. global-camera -position (set -15 10 10))
    (.lookAt global-camera (.-position global-scene))
    (render-objects!)))

(defn main! [] (initialize-canvas!) (println "App started."))

(defn reset-scene! []
  (doseq [[k obj] @*global-objects] (.remove global-scene obj))
  (reset! *global-objects {}))

(defn reload! [] (reset-scene!) (render-objects!) (println "Code updated."))

(set! (.-onload js/window) main!)
