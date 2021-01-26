(ns russellmays-com.web
  (:require [clojure.string :as str]
            [hiccup.page :refer [html5]]
            [me.raynes.cegdown :as md]
            [optimus.assets :as assets]
            [optimus.export :as o.export]
            [optimus.link :as link]
            [optimus.optimizations :as optimizations]
            [optimus.prime :as optimus]
            [optimus.strategies :refer [serve-live-assets]]
            [stasis.core :as stasis]))

(defn ^:private index-page
  "russellmays.com"
  [request]
  (html5
   [:head
    [:title "Russell Mays"]
    [:meta {:charset "utf-8"}]
    [:meta {:name    "viewport"
            :content "width=device-width, initial-scale=1.0"}]
    [:link {:rel  "shortcut icon"
            :type "image/png"
            :href (link/file-path request "/russellmays.png")}]
    [:link {:rel "stylesheet"
            :type "text/css"
            :href (link/file-path request "/style.css")}]]
   [:body
    [:div.avatar-container
     [:img {:src (link/file-path request "/avatar-scaled.png")}]]
    [:div.about
     [:div.about-section
      (str/join " "
                ["I'm a software engineer at"
                 (html5 [:a {:href "https://highspot.com"} "Highspot"])
                 "where we're empowering revenue teams with better content and better customer conversations."])]
     [:hr.about-divider]
     [:div.about-footer
      (str/join " "
                ["Connect with me on"
                 (html5 [:a {:href "https://www.linkedin.com/in/russellmays"} "LinkedIn"])])
      [:br]
      (str/join " "
                ["or check out my"
                 (html5 [:a {:href "https://github.com/russellmays"} "GitHub Profile."])])]]]))

(defn ^:private song-of-the-month-page
  "russellmays.com/song-of-the-month/"
  [request]
  "Song of the Month")

(defn ^:private song-page
  "russellmays.com/song-of-the-month/<page>/"
  [page]
  page)

(defn ^:private song-pages
  "Processes individual song pages."
  [pages]
  (zipmap (->> (keys pages)
               (map #(str/replace % #"\.md$" "/"))
               (map #(str "/song-of-the-month" %)))
          (->> (vals pages)
               (map #(md/to-html %))
               (map #(song-page %)))))

(defn ^:private get-pages
  "Returns all site pages."
  []
  (stasis/merge-page-sources
   {:index             {"/index.html" index-page}
    :song-of-the-month {"/song-of-the-month/index.html" song-of-the-month-page}
    :songs             (song-pages
                        (stasis/slurp-directory "resources/blog/song-of-the-month"
                                                #"\.md$"))}))

(defn ^:private wrap-page
  "Handles string or fn pages."
  [page request]
  (if (string? page)
    page
    (page request)))

(defn ^:private wrap-pages
  "Lazy page loading with page request fn shim."
  [pages]
  (zipmap (keys pages)
          (map #(partial wrap-page %) (vals pages))))

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
