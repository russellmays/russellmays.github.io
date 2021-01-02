(defproject russellmays-com "0.1.0-SNAPSHOT"
  :description "russellmays.com source"
  :url "https://russellmays.com"
  :license {:name "MIT"
            :url "https://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [stasis "2.5.0"]
                 [ring "1.8.2"]
                 [hiccup "1.0.5"]
                 [me.raynes/cegdown "0.1.1"]]
  :ring {:handler russellmays-com.web/app}
  :profiles {:dev {:plugins [[lein-ring "0.12.5"]]}})
