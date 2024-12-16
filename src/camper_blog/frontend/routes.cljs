;;  (ns camper-blog.frontend.routes
;;   (:require
;;    [camper-blog.frontend.views.article :refer [article]]
;;    [re-frame.core :refer [dispatch]]
;;    [reagent.core :as r]
;;    [reitit.frontend :as rf]
;;    [reitit.frontend.easy :as rfe]
;;    [re-frame.core :as re-frame]
;;    [camper-blog.frontend.views.home :refer [home]]))

;; (def routes
;;   [["/home"
;;     {:name ::home
;;      :view home}]
;;    #_["/about"
;;       {:name ::about
;;        :view (fn [])}]
;;    ["/article/:id"
;;     {:name ::article
;;        ;:view (fn [] [article @(re-frame/subscribe [:camper-blog.frontend.subs/article])])
;;      }]])

;; (defn init! []
;;   #_(rfe/start!
;;    (rf/router routes {})
;;    (fn [m]
;;      (prn "Mmm" m)
;;      (dispatch [:camper-blog.frontend.events/add-route m]))
;;    {:use-fragment true}))