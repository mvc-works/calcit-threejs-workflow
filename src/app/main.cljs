
(ns app.main
  (:require [app.schema :as schema]
            [cljs.reader :refer [read-string]]
            ["three" :as Three]
            [app.global :as global]
            [app.stage :refer [render-objects!]]
            [app.animate :refer [animation-loop!]]
            [app.interactions :as interactions]))

(def mount-target (.querySelector js/document ".app"))

(defn initialize-canvas! []
  (let [el (.-domElement global/renderer)]
    (.setSize global/renderer (.-innerWidth js/window) (.-innerHeight js/window))
    (.setClearColor global/renderer 0xdddddd 1)
    (.appendChild mount-target el)
    (render-objects!)
    (.addEventListener el "mousedown" #(interactions/on-mousedown! %))
    (.addEventListener el "mousemove" #(interactions/on-mousemove! %))
    (.addEventListener el "mouseup" #(interactions/on-mouseup! %))
    (.requestAnimationFrame js/window animation-loop!)))

(defn main! []
  (initialize-canvas!)
  (println "App started.")
  (set! (.-globalScene js/window) global/scene))

(defn reset-scene! []
  (doseq [[k obj] @global/*objects] (.remove global/scene obj))
  (reset! global/*objects {})
  (set! (.-fog global/scene) nil))

(defn reload! [] (reset-scene!) (render-objects!) (println "Code updated."))

(set! (.-onload js/window) main!)
