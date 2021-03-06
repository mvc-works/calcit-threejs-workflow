
(ns app.stage
  (:require ["three" :as Three]
            ["LoaderSupport" :as LoaderSupport]
            ["STLLoader" :as STLLoader]
            ["OBJLoader2" :as OBJLoader2]
            ["GLTFLoader" :as GLTFLoader]
            [app.global :as global]
            [app.global :refer [add-object!]]
            [app.util :refer [show-size!]]))

(defn generate-line [color vertices]
  (let [geometry (Three/Geometry.)]
    (doseq [v vertices] (.. geometry -vertices (push v)))
    (Three/Line. geometry (Three/LineBasicMaterial. (clj->js {:color color})))))

(defn load-duck! []
  (let [loader (GLTFLoader.)]
    (.load
     loader
     "./entry/duck.gltf"
     (fn [gltf]
       (.traverse
        (.-scene gltf)
        (fn [child]
          (if (= "Mesh" (.-type child))
            (let [duck-mesh (.clone child)]
              (.set (.-position duck-mesh) 0 0 -100)
              (.set (.-scale duck-mesh) 0.1 0.1 0.1)
              (show-size! duck-mesh)
              (do (.log js/console "Child" child) (add-object! {:duck duck-mesh})))))))
     (fn [xhr] (comment .log js/console xhr))
     (fn [error] (.error js/console "GLTF Loading Error:" error)))))

(defn load-teapot! []
  (let [loader (OBJLoader2.)]
    (.load
     loader
     "./entry/teapot.obj"
     (fn [event]
       (.log js/console "teapot" event)
       (let [teapot-mesh (.. event -detail -loaderRootNode)]
         (show-size! teapot-mesh)
         (add-object! {:teapot teapot-mesh})))
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
       (comment .log js/console "geometry" tree-geometry)
       (comment .translate tree-geometry -2.3 0 -2.1)
       (let [tree-mesh (Three/Mesh.
                        tree-geometry
                        (Three/MeshLambertMaterial. (clj->js {:color 0xbbeebb})))]
         (comment set! (.-castShadow tree-mesh) true)
         (.. tree-mesh -position (set 0 0.5 -6))
         (comment .. tree-mesh -scale (set 2 2 2))
         (add-object! {:tree tree-mesh}))))))

(defn render-axis! []
  (let [x-axis (generate-line 0xff0000 [(Three/Vector3. -10 0 0) (Three/Vector3. 10 0 0)])
        y-axis (generate-line 0x00cc00 [(Three/Vector3. 0 -10 0) (Three/Vector3. 0 10 0)])
        z-axis (generate-line 0x0000cc [(Three/Vector3. 0 0 -10) (Three/Vector3. 0 0 10)])]
    (add-object! {:x x-axis, :y y-axis, :z z-axis})))

(defn render-camera! []
  (let [position (:position @global/*camera-info)
        orientation (:orientation @global/*camera-info)]
    (.. global/camera -position (set (.-x position) (.-y position) (.-z position)))
    (.lookAt global/camera (.add (.clone position) orientation))))

(defn render-cube! []
  (let [cube-mesh (Three/Mesh.
                   (Three/BoxGeometry. 2 2 2)
                   (Three/MeshBasicMaterial. (clj->js {:color 0xffffff})))]
    (.log js/console "read mesh:" cube-mesh)
    (.. cube-mesh -position (set 0 0 0))
    (add-object! {:cube cube-mesh})))

(defn render-fog! []
  (let [fog-color (Three/Color. 0xaaaaaa)]
    (set! (.-fog global/scene) (Three/Fog. fog-color 0.1 100))))

(defn render-light! []
  (let [light (Three/PointLight. 0xffffff)]
    (.. light -position (set 6 0 6))
    (set! (.-intensity light) 1.2)
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

(defn render-spot-light! []
  (let [light (Three/SpotLight. 0xffffff)]
    (.. light -position (set 6 0 6))
    (set! (.-castShadow light))
    (set! (-> light .-shadow .-mapSize .-width) 1024)
    (set! (-> light .-shadow .-mapSize .-height) 1024)
    (set! (-> light .-shadow .-camera .-near) 500)
    (comment set! (.-intensity light) 1.5)
    (set! (-> light .-shadow .-camera .-far) 4000)
    (set! (-> light .-shadow .-camera .-fov) 30)
    (.log js/console "target:" (.-target light))
    (-> light .-target .-position (.set 0 0 0))
    (add-object! {:spot-light light})))

(defn render-objects! []
  (render-light!)
  (comment render-spot-light!)
  (comment render-fog!)
  (render-camera!)
  (render-plane!)
  (render-axis!)
  (render-rotation-lines!)
  (render-cube!)
  (load-teapot!)
  (load-tree!)
  (load-duck!)
  (.render global/renderer global/scene global/camera)
  (.log js/console global/scene))
