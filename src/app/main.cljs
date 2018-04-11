
(ns app.main
  (:require [app.schema :as schema]
            [cljs.reader :refer [read-string]]
            ["three" :as Three]
            [app.global :as global]
            [app.stage :refer [render-objects!]]
            [app.animate :refer [animation-loop!]]))

(def mount-target (.querySelector js/document ".app"))

(defn initialize-canvas! []
  (let []
    (.setSize global/renderer (.-innerWidth js/window) (.-innerHeight js/window))
    (.setClearColor global/renderer 0xdddddd 1)
    (.appendChild mount-target (.-domElement global/renderer))
    (render-objects!)
    (.requestAnimationFrame js/window animation-loop!)))

(defn main! [] (initialize-canvas!) (println "App started."))

(defn reset-scene! []
  (doseq [[k obj] @global/*objects] (.remove global/scene obj))
  (reset! global/*objects {}))

(defn reload! [] (reset-scene!) (render-objects!) (println "Code updated."))

(set! (.-onload js/window) main!)
