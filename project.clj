(defproject russellmays-com "0.1.0-SNAPSHOT"
  :description  "russellmays.com source"
  :url          "https://russellmays.com"
  :license      {:name "MIT"
                 :url "https://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [stasis "2.5.0"]
                 [ring "1.8.2"]
                 [hiccup "1.0.5"]
                 [me.raynes/cegdown "0.1.1"]
                 [optimus "0.20.2"]
                 [enlive "1.1.6"]]
  :ring         {:handler russellmays-com.web/app}
  :aliases      {"build-site" ["run" "-m" "russellmays-com.web/export"]}
  :profiles     {:dev  {:plugins [[lein-ring "0.12.5"]]}
                 :test {:dependencies [[midje "1.9.9"]]
                        :plugins      [[lein-midje "3.2.1"]]}})
