(ns clojure-tutorials.frontend.views
  (:require
   ["@mui/material/styles" :as styles-js]
   [clojure-tutorials.frontend.views.header :refer [header]]
   [clojure-tutorials.frontend.views.home :refer [home]]
   [re-frame.core :as re-frame]
   [reagent-mui.components :refer [container]]
   [reagent-mui.material.css-baseline :refer [css-baseline]]
   [reagent-mui.styles :as styles]
   [reagent-mui.util :as mui-util]
   [clojure.string :as str]))

(defn main-panel []
  (fn []
    (let [path (.. js/document -location -pathname)] 
      [:<>
       [styles/experimental-css-vars-provider
        {:theme (styles-js/experimental_extendTheme (mui-util/clj->js' {:cssVarPrefix "blog"
                                                                        :palette {:mode @(re-frame/subscribe [:clojure-tutorials.frontend.subs/palette-mode])}}))}
        [css-baseline]
        [header]
        (when-not (str/starts-with? path "/article/")
          [:main
           [container {:max-width false}
            (if-let [view @(re-frame/subscribe [:clojure-tutorials.frontend.subs/route-view])]
              [view]
              (let [path (.. js/document -location -pathname)]
                (cond 
                  (= "/" path) [home])))]])]])))
