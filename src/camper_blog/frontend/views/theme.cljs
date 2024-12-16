(ns camper-blog.frontend.views.theme
  (:require
   [re-frame.core :as re-frame]
   [reagent-mui.material.switch :refer [switch]]))

(defn apply-theme [mode]
  (js/document.documentElement.setAttribute "data-color-scheme" mode)
  (re-frame/dispatch [:camper-blog.frontend.events/palette-mode mode]))

(defn theme-switch []
  [:<> 
   [:label.sr-only {:for "theme-switch"} "Cambiar tema"]
   [switch {:id "theme-switch"
            :class "material-ui-switch"
            :checked (= "dark" @(re-frame/subscribe [:camper-blog.frontend.subs/palette-mode]))
            :on-change #(let [mode (if %2 "dark" "light")]
                          (apply-theme mode)
                          (.setItem js/localStorage "data-color-scheme" mode))}]])
