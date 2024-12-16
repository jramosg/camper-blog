(ns camper-blog.frontend.views.header
  (:require
   [camper-blog.frontend.views.theme :refer [theme-switch]]
   [re-frame.core :as re-frame] ;[reagent-mui.components :refer [input-base]]
   [reagent-mui.icons.navigate-before :refer [navigate-before]]
   [reagent-mui.icons.navigate-next :refer [navigate-next]] ;[reagent-mui.icons.search :refer [search] :rename {search search-icon}]
   [reagent-mui.material.app-bar :refer [app-bar]]
   [reagent-mui.material.icon-button :refer [icon-button]]
   [reagent-mui.material.link :refer [link]]
   [reagent-mui.material.toolbar :refer [toolbar]]
   [reagent-mui.material.tooltip :refer [tooltip]]))

(defn header []
  (re-frame/dispatch [:camper-blog.frontend.events/load-articles])
  (fn []
    (let [[path ] @(re-frame/subscribe [:camper-blog.frontend.subs/route])
          {:keys [id]} @(re-frame/subscribe [:camper-blog.frontend.subs/current-article])
          by-id @(re-frame/subscribe [:camper-blog.frontend.subs/articles-by-id])
          articles-count (if (= id 1)
                  (get-in by-id [@(re-frame/subscribe [:camper-blog.frontend.subs/articles-count]) :slug])
                  (get-in by-id [(dec id) :slug]))
          prv-article (if (= id 1)
                        (get by-id articles-count)
                        (get by-id (dec id)))
          next-article (if (= id articles-count)
                         (get by-id 1)
                         (get by-id (inc id)))]
      [app-bar {:position :static
                :enable-color-on-dark true}
       [toolbar
        [link {:variant :h6
               :no-wrap true
               :href "/"
               :aria-label "Ver índice"
               :color :inherit
               :underline :hover}
         "Guía de Furgonetas Camper"]
        #_[:div.search-wrapper
           [:div.search-icon-wrapper
            [search-icon]]
           [input-base {:placeholder "Buscar..."}]]
        [:div.header-action-container
         (when (and path (not (#{["politica-de-cookies"]
                                 ["politica-de-privacidad"]
                                 ["sobre-nosotros"]} path)))
           [:<>
           [tooltip {:title (:title prv-article)}
            [icon-button {:color "inherit"
                          :aria-label "Artículo previo"
                          :href (str "/" (:slug prv-article))}
             [navigate-before]]]
            [tooltip {:title (:title next-article)}
             [icon-button {:color "inherit"
                           :aria-label (:title next-article)
                           :href (str "/" (:slug next-article))}
              [navigate-next]]]])
         [theme-switch]]]])))