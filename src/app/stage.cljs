
(ns app.stage
  (:require ["three" :as Three]
            ["LoaderSupport" :as LoaderSupport]
            ["STLLoader" :as STLLoader]
            ["OBJLoader2" :as OBJLoader2]
            [app.global :as global]
            [app.global :refer [add-object!]]))

(defn generate-line [color vertices]
  (let [geometry (Three/Geometry.)]
    (doseq [v vertices] (.. geometry -vertices (push v)))
    (Three/Line. geometry (Three/LineBasicMaterial. (clj->js {:color color})))))

(defn load-teapot! []
  (let [loader (OBJLoader2.)]
    (.load
     loader
     "./entry/teapot.obj"
     (fn [event]
       (.log js/console "teapot" event)
       (add-object! {:teapot (.. event -detail -loaderRootNode)}))
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
         (add-object! {:tree tree-mesh}))))))

(defn render-axis! []
  (let [x-axis (generate-line 0xff0000 [(Three/Vector3. -10 0 0) (Three/Vector3. 10 0 0)])
        y-axis (generate-line 0x00cc00 [(Three/Vector3. 0 -10 0) (Three/Vector3. 0 10 0)])
        z-axis (generate-line 0x0000cc [(Three/Vector3. 0 0 -10) (Three/Vector3. 0 0 10)])]
    (add-object! {:x x-axis, :y y-axis, :z z-axis})))

(defn render-camera! []
  (.. global/camera -position (set -8 8 20))
  (.lookAt global/camera (.-position global/scene)))

(defn render-cube! []
  (let [cube-mesh (Three/Mesh.
                   (Three/BoxGeometry. 2 2 2)
                   (Three/MeshBasicMaterial. (clj->js {:color 0xffff00})))]
    (.. cube-mesh -position (set 0 0 0))
    (add-object! {:cube cube-mesh})))

(defn render-light! []
  (let [light (Three/PointLight. 0xffffff)]
    (.. light -position (set 0 0 6))
    (add-object! {:light light})))

(defn render-plane! []
  (let [plane-mesh (Three/Mesh.
                    (Three/PlaneGeometry. 10 10 5 5)
                    (Three/MeshBasicMaterial.
                     (clj->js {:color 0xffff00, :side Three/DoubleSide})))]
    (set! (.. plane-mesh -rotation -x) (/ js/Math.PI 2))
    (add-object! {:plane plane-mesh})))

(defn render-rotation-lines! []
  (add-object!
   {:original-line (generate-line
                    0x222222
                    [(Three/Vector3. 0 0 0) (Three/Vector3. 10 10 10)])}))

(defn render-objects! []
  (render-light!)
  (render-camera!)
  (comment render-plane!)
  (render-axis!)
  (render-rotation-lines!)
  (comment render-cube!)
  (comment load-teapot!)
  (comment load-tree!)
  (.render global/renderer global/scene global/camera)
  (.log js/console 1))
