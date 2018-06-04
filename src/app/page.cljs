
(ns app.page
  (:require [respo.render.html :refer [make-string]]
            [shell-page.core :refer [make-page spit slurp]]
            [app.schema :as schema]
            [cljs.reader :refer [read-string]]
            [app.util :refer [get-env!]]
            [app.config :as config]))

(def base-info
  {:title (:title config/site), :icon (:icon config/site), :ssr nil, :inline-html nil})

(defn dev-page []
  (make-page
   ""
   (merge
    base-info
    {:styles [], :scripts ["/client.js"], :inline-styles [(slurp "./entry/main.css")]})))

(def local-bundle? (get-env! "mode"))

(defn prod-page []
  (make-page
   ""
   (let [assets (read-string (slurp "dist/assets.edn"))
         cdn (if local-bundle? "" (:cdn-url config/site))
         prefix-cdn (fn [x] (str cdn x))]
     (merge
      base-info
      {:styles [],
       :scripts (map #(-> % :output-name prefix-cdn) assets),
       :inline-styles [(slurp "./entry/main.css")]}))))

(defn main! []
  (if (contains? config/bundle-builds (get-env! "mode"))
    (spit "dist/index.html" (prod-page))
    (spit "target/index.html" (dev-page))))
