(ns clojure-tutorials.frontend.subs
  (:require
   [re-frame.core :as re-frame]))

(re-frame/reg-sub
 ::articles
 (fn [db]
   (:articles db)))

(re-frame/reg-sub
 ::sorted-articles
 :<- [::articles]
 (fn [articles]
   (sort-by (comp js/parseInt :id val) articles)))

(re-frame/reg-sub
 ::article
 :<- [::articles]
 :<- [::path-params]
 (fn [[articles {:keys [id]}]]
   (get articles id)))

(re-frame/reg-sub
 ::article-id
 :<- [::article] 
 :-> :id)

(re-frame/reg-sub
 ::route
 :-> :route)

(re-frame/reg-sub
 ::route-view
 :<- [::route]
 :-> (comp :view :data))

(re-frame/reg-sub
 ::path-params
 :<- [::route]
 :-> :path-params)

(re-frame/reg-sub
 ::palette-mode
 (fn [db]
   (or (js/localStorage.getItem "color-scheme")
       (get-in db [:palette :mode] "light"))))
