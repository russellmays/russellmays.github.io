(ns russellmays-com.web
  (:require [clojure.string :as str]
            [hiccup.page :refer [html5]]
            [me.raynes.cegdown :as md]
            [optimus.assets :as assets]
            [optimus.export :as o.export]
            [optimus.optimizations :as optimizations]
            [optimus.prime :as optimus]
            [optimus.strategies :refer [serve-live-assets]]
            [stasis.core :as stasis]))

(defn ^:private layout-page
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

(defn ^:private partial-pages
  ""
  [pages]
  (zipmap (keys pages)
          (map layout-page (vals pages))))

(defn ^:private markdown-pages
  ""
  [pages]
  (zipmap (map #(str/replace % #"\.md$" "/") (keys pages))
          (map #(layout-page (md/to-html %)) (vals pages))))

(defn ^:private get-pages
  ""
  []
  (stasis/merge-page-sources
   {:public   (stasis/slurp-directory "resources/public" #".*\.html$")
    :partials (partial-pages (stasis/slurp-directory "resources/partials" #".*\.html$"))
    :markdown (markdown-pages (stasis/slurp-directory "resources/md" #"\.md$"))}))

(defn ^:private wrap-pages
  "Lazy page loading with page request fn shim."
  [pages]
  (zipmap (keys pages)
          (map #(fn [req] (% req)) (vals pages))))

(defn ^:private get-assets
  "Pulls in assets."
  []
  (concat
   (assets/load-assets "public" ["/style.css"])
   (assets/load-assets "images" ["/russellmays.png"
                                 "/avatar-scaled.png"])))

(def app
  "Orchestrates pages and assets for local development."
  (optimus/wrap (-> (get-pages)
                    wrap-pages
                    stasis/serve-pages)
                get-assets
                optimizations/all
                serve-live-assets))

(defn export
  "Builds pages and assets for production deployment."
  []
  (let [export-dir "dist"
        assets     (optimizations/all (get-assets) {})
        assets     (remove #(:outdated %) assets)]
    (stasis/empty-directory! export-dir)
    (o.export/save-assets assets export-dir)
    (stasis/export-pages (get-pages) export-dir {:optimus-assets assets})))
