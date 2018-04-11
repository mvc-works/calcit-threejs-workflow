
(ns app.main
  (:require [app.schema :as schema]
            [cljs.reader :refer [read-string]]
            ["three" :as Three]
            ["LoaderSupport" :as LoaderSupport]
            ["OBJLoader2" :as OBJLoader2]
            ["STLLoader" :as STLLoader]))

(defonce *global-objects (atom {}))

(defonce *global-values (atom {:time 0}))

(defonce global-scene (new Three/Scene))

(defn add-scene-object! [pairs]
  (doseq [[k obj] pairs] (.add global-scene obj))
  (swap! *global-objects merge pairs))

(defonce global-camera
  (Three/PerspectiveCamera.
   50
   (/ (.-innerWidth js/window) (.-innerHeight js/window))
   0.1
   1000))

(defonce global-renderer (new Three/WebGLRenderer))

(defn animation-loop! [timestamp]
  (let [tree (:teapot @*global-objects)]
    (when (and (some? tree))
      (swap! *global-values assoc :time timestamp)
      (set! (.. tree -rotation -y) (/ (:time @*global-values) 1000))
      (.render global-renderer global-scene global-camera)))
  (.requestAnimationFrame js/window animation-loop!))

(def mount-target (.querySelector js/document ".app"))

(defn render-objects! []
  (.log js/console Three/ObjectLoader)
  (let [cube-mesh (Three/Mesh.
                   (Three/BoxGeometry. 2 2 2)
                   (Three/MeshLambertMaterial. (clj->js {:color 0x55aa55})))
        light (Three/PointLight. 0xffffff)
        loader (OBJLoader2.)
        stl-loader (STLLoader.)
        plane-mesh (Three/Mesh.
                    (Three/PlaneGeometry. 10 10 5 5)
                    (Three/MeshBasicMaterial.
                     (clj->js {:color :ffff00, :side Three/DoubleSide})))]
    (.. light -position (set 0 0 6))
    (.. cube-mesh -position (set 4 2 0))
    (set! (.. plane-mesh -rotation -x) (/ js/Math.PI 2))
    (add-scene-object! {:cube cube-mesh, :light light, :ground plane-mesh})
    (.load
     loader
     "/entry/teapot.obj"
     (fn [event]
       (.log js/console "teapot" event)
       (add-scene-object! {:teapot (.. event -detail -loaderRootNode)}))
     nil
     nil
     nil
     false)
    (.load
     stl-loader
     "/entry/tree.stl"
     (fn [tree-geometry]
       (let [tree-mesh (Three/Mesh.
                        tree-geometry
                        (Three/MeshLambertMaterial. (clj->js {:color 0x55aa55})))]
         (set! (.-castShadow tree-mesh) true)
         (.. tree-mesh -position (set 0 3 0))
         (.. tree-mesh -scale (set 2 2 2))
         (add-scene-object! {:tree tree-mesh}))))
    (.log js/console 1)))

(defn initialize-canvas! []
  (let []
    (.setSize global-renderer (.-innerWidth js/window) (.-innerHeight js/window))
    (.setClearColor global-renderer 0xdddddd 1)
    (.appendChild mount-target (.-domElement global-renderer))
    (.. global-camera -position (set 0 10 20))
    (.lookAt global-camera (.-position global-scene))
    (render-objects!)
    (.requestAnimationFrame js/window animation-loop!)))

(defn main! [] (initialize-canvas!) (println "App started."))

(defn reset-scene! []
  (doseq [[k obj] @*global-objects] (.remove global-scene obj))
  (reset! *global-objects {}))

(defn reload! [] (reset-scene!) (render-objects!) (println "Code updated."))

(set! (.-onload js/window) main!)
