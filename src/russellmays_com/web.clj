(ns russellmays-com.web
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [hiccup.page :refer [html5]]
            [me.raynes.cegdown :as md]
            [stasis.core :as stasis]))

(defn layout-page
  ""
  [page]
  (html5
   [:head
    [:meta {:charset "utf-8"}]
    [:meta {:name "viewport"
            :content "width=device-width, initial-scale=1.0"}]
    [:title "Tech blog"]
    [:link {:rel "stylesheet" :href "/styles/styles.css"}]]
   [:body
    [:div.logo "russellmays.com"]
    [:div.body page]]))

(defn partial-pages
  ""
  [pages]
  (zipmap (keys pages)
          (map layout-page (vals pages))))

(defn markdown-pages
  ""
  [pages]
  (zipmap (map #(str/replace % #"\.md$" "/") (keys pages))
          (map #(layout-page (md/to-html %)) (vals pages))))

(defn get-pages
  ""
  []
  (stasis/merge-page-sources
   {:public   (stasis/slurp-directory "resources/public" #".*\.(html|css|js)$")
    :partials (partial-pages (stasis/slurp-directory "resources/partials" #".*\.html$"))
    :markdown (markdown-pages (stasis/slurp-directory "resources/md" #"\.md$"))}))

(def app (stasis/serve-pages get-pages))
