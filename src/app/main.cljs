
(ns app.main
  (:require [app.schema :as schema] [cljs.reader :refer [read-string]] ["three" :as Three]))

(defonce *global-objects (atom {}))

(defonce global-camera (Three/PerspectiveCamera. 35 (/ 1200 800) 0.1 1000))

(defonce global-renderer (new Three/WebGLRenderer))

(defonce global-scene (new Three/Scene))

(def mount-target (.querySelector js/document ".app"))

(defn render-objects! []
  (let [geometry (Three/BoxGeometry. 5 5 5)
        material (Three/MeshLambertMaterial. (clj->js {:color 0xff0000}))
        mesh (Three/Mesh. geometry material)
        light (new Three/PointLight 0xffff00)]
    (swap! *global-objects assoc :light light :mesh mesh)
    (.. light -position (set 10 0 10))
    (.add global-scene light)
    (.. mesh -position (set (rand-int 10) (rand-int 10) (rand-int 10)))
    (.add global-scene mesh)
    (.log js/console 1)
    (.render global-renderer global-scene global-camera)))

(defn initialize-canvas! []
  (let []
    (.setSize global-renderer 1200 800)
    (.setClearColor global-renderer 0xdddddd 1)
    (.appendChild mount-target (.-domElement global-renderer))
    (.. global-camera -position (set -15 10 10))
    (.lookAt global-camera (.-position global-scene))
    (render-objects!)))

(defn main! [] (initialize-canvas!) (println "App started."))

(defn reset-scene! [] (doseq [[k obj] @*global-objects] (.remove global-scene obj)))

(defn reload! [] (reset-scene!) (render-objects!) (println "Code updated."))

(set! (.-onload js/window) main!)
