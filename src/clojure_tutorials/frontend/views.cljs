(ns clojure-tutorials.frontend.views
  (:require
   ["@mui/material/styles" :as styles-js]
   [clojure-tutorials.frontend.views.theme :as theme]
   [clojure-tutorials.frontend.views.cookies :as cookies]
   [clojure-tutorials.frontend.views.header :refer [header]]
   [clojure.string :as str]
   [re-frame.core :as re-frame]
   [reagent-mui.material.css-baseline :refer [css-baseline]]
   [reagent-mui.styles :as styles]
   [reagent-mui.util :as mui-util]))

(defn main-panel []
  (re-frame/dispatch [:clojure-tutorials.frontend.events/add-route
                      (-> (.. js/document -location -pathname)
                          (str/split  #"/")
                          next
                          vec)])
  (theme/apply-theme (or (.getItem js/localStorage "data-color-scheme") "light"))
  (let [button (.getElementById js/document  "unaccept-cookies")]
    (.addEventListener button "click" cookies/unaccept-cookies))
  (fn []
    [:<>
     [styles/experimental-css-vars-provider
      {:theme (styles-js/experimental_extendTheme (mui-util/clj->js' {:cssVarPrefix "blog"
                                                                      :palette {:mode @(re-frame/subscribe [:clojure-tutorials.frontend.subs/palette-mode])}}))}
      [css-baseline]
      [header]
      [cookies/cookies-dialogs]
      [cookies/unnacept-dialog]]]))
