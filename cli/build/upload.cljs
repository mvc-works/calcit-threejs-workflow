
(ns build.upload
  (:require ["child_process" :as cp]
            [app.config :as config]))

(defn sh! [command]
  (println command)
  (println (.toString (cp/execSync command))))

(defn -main []
  (sh! "cp -r entry dist/")
  (sh! (str "rsync -avr --progress dist/* " (:cdn-folder config/site)))
  (sh! (str "rsync -avr --progress dist/{index.html,manifest.json,entry} "
            (:upload-folder config/site))))
