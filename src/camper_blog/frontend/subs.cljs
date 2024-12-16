(ns camper-blog.frontend.subs
  (:require
   [re-frame.core :as re-frame]))

(re-frame/reg-sub
 ::articles
 (fn [db]
   (:articles db)))

(re-frame/reg-sub
 ::articles-by-slug
  :<- [::articles]
 (fn [articles]
   (reduce #(assoc %1 (str (:slug %2)) %2) {} articles)))

(re-frame/reg-sub
 ::articles-by-id
 :<- [::articles]
 (fn [articles]
   (reduce #(assoc %1 (:id %2) %2) {} articles)))

(re-frame/reg-sub
 ::articles-count
 :<- [::articles]
 :-> count)

(re-frame/reg-sub
 ::route
 :-> :route)

(re-frame/reg-sub
 ::current-article
 :<- [::articles-by-slug]
 :<- [::route]
 (fn [[articles [route]]]
   (get articles route)))

(re-frame/reg-sub
 ::palette-mode
 (fn [db]
   (or (.getItem js/localStorage "data-color-scheme")
       (get-in db [:palette :mode] "light"))))
