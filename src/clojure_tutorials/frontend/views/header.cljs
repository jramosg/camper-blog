(ns clojure-tutorials.frontend.views.header
  (:require
   [clojure-tutorials.frontend.views.theme :refer [theme-switch]]
   [re-frame.core :as re-frame] ;[reagent-mui.components :refer [input-base]]
   [reagent-mui.icons.navigate-before :refer [navigate-before]]
   [reagent-mui.icons.navigate-next :refer [navigate-next]] ;[reagent-mui.icons.search :refer [search] :rename {search search-icon}]
   [reagent-mui.material.app-bar :refer [app-bar]]
   [reagent-mui.material.icon-button :refer [icon-button]]
   [reagent-mui.material.link :refer [link]]
   [reagent-mui.material.toolbar :refer [toolbar]]))

(defn header [] 
  (re-frame/dispatch [:clojure-tutorials.frontend.events/load-articles])
  (fn []
    (let [[path :as route] @(re-frame/subscribe [:clojure-tutorials.frontend.subs/route])] 
      (prn "c" @(re-frame/subscribe [:clojure-tutorials.frontend.subs/articles-count]))
      [app-bar {:position :static
                :enable-color-on-dark true}
       [toolbar
        [link {:variant :h6
               :no-wrap true
               :href "/"
               :aria-label "Ver índice"
               :color :inherit
               :underline :hover} "Clojure: tutoriales"]
        #_[:div.search-wrapper
           [:div.search-icon-wrapper
            [search-icon]]
           [input-base {:placeholder "Buscar..."}]]
        [:div.header-action-container
         (when (= path "article")
           (let [id (js/parseInt (second route))]
             [:<>
              [icon-button {:color "inherit"
                            :aria-label "Artículo previo"
                            :href (str "/article/" (if (= id 1)
                                                     150 (dec id)))}
               [navigate-before]]
              [icon-button {:color "inherit"
                            :aria-label "Siguient artículo"
                            :href (str "/article/" (if (= id @(re-frame/subscribe [:clojure-tutorials.frontend.subs/articles-count]))
                                                     1 (inc id)))}
               [navigate-next]]]))
         [theme-switch]]]])))