
(ns app.render
  (:require [respo.render.html :refer [make-string]]
            [shell-page.core :refer [make-page spit slurp]]
            [app.schema :as schema]
            [cljs.reader :refer [read-string]]))

(def base-info
  {:title "Calcit", :icon "http://cdn.tiye.me/logo/mvc-works.png", :ssr nil, :inline-html nil})

(defn dev-page []
  (make-page
   ""
   (merge
    base-info
    {:styles [], :scripts ["/main.js"], :inline-styles [(slurp "./entry/main.css")]})))

(def preview? (= "preview" js/process.env.prod))

(defn prod-page []
  (make-page
   ""
   (let [assets (read-string (slurp "dist/assets.edn"))
         cdn (if preview? "" "http://cdn.tiye.me/calcit-threejs-workflow/")
         prefix-cdn (fn [x] (str cdn x))]
     (merge
      base-info
      {:styles [],
       :scripts (map #(-> % :output-name prefix-cdn) assets),
       :inline-styles [(slurp "./entry/main.css")]}))))

(defn main! []
  (if (= "dev" js/process.env.env)
    (spit "target/index.html" (dev-page))
    (spit "dist/index.html" (prod-page))))
