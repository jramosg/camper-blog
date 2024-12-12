(ns clojure-tutorials.frontend.views.header
  (:require
   [re-frame.core :as re-frame]
   [reagent-mui.components :refer [input-base]]
   [reagent-mui.icons.search :refer [search] :rename {search search-icon}]
   [reagent-mui.material.app-bar :refer [app-bar]]
   [reagent-mui.material.link :refer [link]]
   [reagent-mui.material.switch :refer [switch]]
   [reagent-mui.material.toolbar :refer [toolbar]]))

(defn header []
  [app-bar {:position :static
            :enable-color-on-dark true}
   [toolbar
    
    [link {:variant :h6 
           :no-wrap true
           :href "/"
           :color :inherit
           :underline :hover} "Clojure: Funcionalidades"] 
    [:div.search-wrapper 
     [:div.search-icon-wrapper
      [search-icon]]
     [input-base {:placeholder "Buscar..."}]]
   [:div.header-action-container
    [switch {:class "material-ui-switch"
             :checked (= "dark" @(re-frame/subscribe [:clojure-tutorials.frontend.subs/palette-mode]))
             :on-change #(let [mode (if %2 "dark" "light")]
                           (re-frame/dispatch [:clojure-tutorials.frontend.events/palette-mode mode])
                           (js/document.documentElement.setAttribute "data-mui-color-scheme" mode)
                           (js/localStorage.setItem "color-scheme" mode))}]]]])