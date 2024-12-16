(ns clojure-tutorials.frontend.core
  (:require
   [clojure-tutorials.frontend.config :as config]
   [clojure-tutorials.frontend.events]
   [clojure-tutorials.frontend.subs]
   [clojure-tutorials.frontend.views :as views]
   [re-frame.core :as re-frame]
   [reagent.dom :as rdom]))

(defn dev-setup []
  (when config/debug?
    (println "dev mode")))

(defn ^:dev/after-load mount-root []
  (re-frame/clear-subscription-cache!)
  (let [root-el (.getElementById js/document "app")]
    (rdom/unmount-component-at-node root-el)
    (rdom/render [views/main-panel] root-el)))

(defn init []
  (dev-setup)
  ;(routes/init!)
  (mount-root))
