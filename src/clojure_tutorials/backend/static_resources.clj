(ns clojure-tutorials.backend.static-resources
  (:require
   [clojure.edn :as edn]
   [clojure.java.io :as io]
   [clojure.string :as str]
   [hiccup2.core :as h]
   [hiccup.core :as hiccup]))


(defn- load-edn [source]
  (with-open [r (io/reader source)]
    (edn/read (java.io.PushbackReader. r))))

(defn save-html [path content]
  (let [file (io/file path)]
    (io/make-parents file)
    (spit file content)))

(defn static-htmls []
  (doseq [{:keys [title desc id]} (load-edn "resources/public/articles.edn")]
    (let [path (str "resources/public/article/" id "/index.html")
          content (h/html
                   [:head
                    [:meta {:charset "utf-8"}]
                    [:title (str/replace title #"<[^>]*>" "")]
                    [:link {:rel "stylesheet" :href "../styles.css"}]
                    [:meta {:name "viewport" :content "initial-scale=1, width=device-width"}]
                    [:link {:rel "preconnect" :href "https://fonts.googleapis.com"}]
                    [:link {:rel "preconnect" :href "https://fonts.gstatic.com" :crossorigin true}]
                    [:link
                     {:href
                      "https://fonts.googleapis.com/css2?family=Roboto+Mono:ital,wght@0,100..700;1,100..700&family=Roboto:ital,wght@0,100;0,300;0,400;0,500;0,700;0,900;1,100;1,300;1,400;1,500;1,700;1,900&display=swap"
                      :rel "stylesheet"}]]
                   [:body
                    [:noscript "Clojure-tutorials is a JavaScript app. Please enable JavaScript to
      continue."]
                    [:div#app]
                    [:main 
                     [:article
                      [:h2 (h/raw title)]
                      (h/raw desc)]]
                    [:script {:src "/js/compiled/app.js"}]])]
      (save-html path content))))

(comment
  (h/html [:a 1])
  (static-htmls)
  (io/delete-file "/resources/public/article"))