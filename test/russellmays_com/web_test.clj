(ns russellmays-com.web-test
  (:require [midje.sweet :refer :all]
            [net.cgrand.enlive-html :as enlive]
            [russellmays-com.web :as web]))

(fact
 "All pages respond with 200 OK"

 (doseq [url (keys (#'web/get-pages))]
   (let [status (:status (web/app {:uri url}))]
     [url status] => [url 200])))

(defn ^:private link-valid?
  [pages link]
  (let [href (get-in link [:attrs :href])]
    (or
     (not (.startsWith href "/"))
     (contains? pages href)
     (contains? pages (str href "index.html")))))

(fact
 "All links are valid"

 (let [pages (#'web/get-pages)]
   (doseq [url  (keys pages)
           link (-> (web/app {:uri url})
                    :body
                    java.io.StringReader.
                    enlive/html-resource
                    (enlive/select [:a]))]
     (let [href (get-in link [:attrs :href])]
       [url href (link-valid? pages link)] => [url href true]))))
