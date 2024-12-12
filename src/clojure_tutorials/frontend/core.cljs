(ns clojure-tutorials.frontend.core
  (:require
   [clojure-tutorials.frontend.config :as config]
   [clojure-tutorials.frontend.events :as events]
   [clojure-tutorials.frontend.routes :as routes]
   [clojure-tutorials.frontend.views :as views]
   [clojure-tutorials.frontend.subs]
   [re-frame.core :as re-frame]
   [reagent.dom :as rdom]))

(defn dev-setup []
  (when config/debug?
    (println "dev mode")))

(defn ^:dev/after-load mount-root []
  (re-frame/clear-subscription-cache!)
  (let [root-el (.getElementById js/document "app")
        color-scheme (js/localStorage.getItem "color-scheme")]
    (rdom/unmount-component-at-node root-el)
    (js/window.addEventListener "load" (fn [] (js/document.documentElement.setAttribute "data-mui-color-scheme" color-scheme)))
    (rdom/render [views/main-panel] root-el)))

(defn init []
  (dev-setup) 
  (routes/init!)
  (mount-root))
