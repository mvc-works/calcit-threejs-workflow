
(ns app.main
  (:require [app.schema :as schema]
            [cljs.reader :refer [read-string]]
            ["three" :as Three]
            ["LoaderSupport" :as LoaderSupport]
            ["OBJLoader2" :as OBJLoader2]
            ["STLLoader" :as STLLoader]))

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
  (let [geometry (Three/BoxGeometry. 1 1 1)
        material (Three/MeshLambertMaterial. (clj->js {:color 0xff0000}))
        mesh (Three/Mesh. geometry material)
        light (Three/PointLight. 0xffffff)
        loader (OBJLoader2.)
        stl-loader (STLLoader.)]
    (.. light -position (set 0 2 3))
    (.. mesh -position (set 0 0 -2))
    (add-scene-object! {:mesh mesh, :light light})
    (.load
     loader
     "/entry/tree.obj"
     (fn [event]
       (.log js/console "teapot" event)
       (comment add-scene-object! {:teapot (.. event -detail -loaderRootNode)}))
     nil
     nil
     nil
     false)
    (.load
     stl-loader
     "/entry/tree.stl"
     (fn [deer-geometry]
       (.log js/console "deer" deer-geometry)
       (let [deer-mesh (Three/Mesh. deer-geometry material)]
         (set! (.-castShadow deer-mesh) true)
         (.. deer-mesh -position (set -4 -2 -4))
         (.. deer-mesh -scale (set 2 2 2))
         (add-scene-object! {:deer deer-mesh}))))
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
