
(ns app.stage
  (:require ["three" :as Three]
            ["LoaderSupport" :as LoaderSupport]
            ["STLLoader" :as STLLoader]
            ["OBJLoader2" :as OBJLoader2]
            [app.global :as global]
            [app.global :refer [add-scene-object!]]))

(defn load-teapot! []
  (let [loader (OBJLoader2.)]
    (.load
     loader
     "./entry/teapot.obj"
     (fn [event]
       (.log js/console "teapot" event)
       (add-scene-object! {:teapot (.. event -detail -loaderRootNode)}))
     nil
     nil
     nil
     false)))

(defn load-tree! []
  (let [stl-loader (STLLoader.)]
    (.load
     stl-loader
     "./entry/tree.stl"
     (fn [tree-geometry]
       (.log js/console "geometry" tree-geometry)
       (.translate tree-geometry -2.3 0 -2.1)
       (let [tree-mesh (Three/Mesh.
                        tree-geometry
                        (Three/MeshLambertMaterial. (clj->js {:color 0xbbeebb})))]
         (set! (.-castShadow tree-mesh) true)
         (.. tree-mesh -position (set 0 0.5 0))
         (.. tree-mesh -scale (set 2 2 2))
         (add-scene-object! {:tree tree-mesh}))))))

(defn render-camera! []
  (.. global/camera -position (set 0 8 14))
  (.lookAt global/camera (.-position global/scene)))

(defn render-cube! []
  (let [cube-mesh (Three/Mesh.
                   (Three/BoxGeometry. 2 2 2)
                   (Three/MeshLambertMaterial. (clj->js {:color 0x55aa55})))]
    (.. cube-mesh -position (set 4 2 0))
    (add-scene-object! {:cube cube-mesh})))

(defn render-light! []
  (let [light (Three/PointLight. 0xffffff)]
    (.. light -position (set 0 0 6))
    (add-scene-object! {:light light})))

(defn render-plane! []
  (let [plane-mesh (Three/Mesh.
                    (Three/PlaneGeometry. 10 10 5 5)
                    (Three/MeshBasicMaterial.
                     (clj->js {:color :ffff00, :side Three/DoubleSide})))]
    (set! (.. plane-mesh -rotation -x) (/ js/Math.PI 2))
    (add-scene-object! {:plane plane-mesh})))

(defn render-objects! []
  (render-light!)
  (render-plane!)
  (comment render-cube!)
  (render-camera!)
  (comment load-teapot!)
  (load-tree!)
  (.log js/console 1))
