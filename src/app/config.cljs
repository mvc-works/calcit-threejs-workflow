
(ns app.config (:require [app.util :refer [get-env!]]))

(def bundle-builds #{"release" "local-bundle"})

(def dev?
  (if (exists? js/window)
    (do ^boolean js/goog.DEBUG)
    (contains? bundle-builds (get-env! "mode"))))

(def site
  {:title "Three",
   :icon "http://cdn.tiye.me/logo/mvc-works.png",
   :cdn-url "http://cdn.tiye.me/calcit-threejs-workflow/"})
