(ns clojure-tutorials.frontend.subs
  (:require
   [re-frame.core :as re-frame]))

(re-frame/reg-sub
 ::articles
 (fn [db]
   (:articles db)))

(re-frame/reg-sub
 ::articles
 (fn [db]
   (:articles db)))

(re-frame/reg-sub
 ::articles-count
 :<- [::articles]
 :-> count)

(re-frame/reg-sub
 ::route
 :-> :route)

(re-frame/reg-sub
 ::palette-mode
 (fn [db]
   (or (.getItem js/localStorage "data-color-scheme")
       (get-in db [:palette :mode] "light"))))
