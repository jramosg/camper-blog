(ns clojure-tutorials.frontend.events
  (:require
   [re-frame.core :as rf]
   [day8.re-frame.http-fx]
   [ajax.edn :as ajax]))

(rf/reg-event-fx
 ::load-articles
 (fn [{:keys [db]}]
   {:fx [[:http-xhrio {:method :get
                       :uri "/articles.edn"
                       :response-format (ajax/edn-response-format)
                       :on-success [::articles-loaded]
                       :on-failer [::articles-failed]}]]
    :db (assoc db :articles {:loading? true})}))

(rf/reg-event-db
 ::articles-loaded
 (fn [db [_ articles]]
   (assoc db :articles (reduce #(assoc %1 (str (:id %2)) %2) {} articles))))

(rf/reg-event-db
 ::articles-failed
 (fn [db]
   db))

(rf/reg-event-db
 ::add-route
 (fn [db [_ route]]
   (assoc db :route route)))

(rf/reg-event-db
 ::palette-mode 
 (fn [db [_ mode]]
   (assoc-in db [:palette :mode] mode)))
