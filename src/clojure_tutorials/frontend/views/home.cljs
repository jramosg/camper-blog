(ns clojure-tutorials.frontend.views.home
  (:require
   [re-frame.core :as re-frame]
   [reagent-mui.material.backdrop :refer [backdrop]]
   [reagent-mui.material.circular-progress :refer [circular-progress]]
   [reagent-mui.material.link :refer [link]]
   [reagent-mui.material.list :refer [list]]
   [reagent-mui.material.list-item :refer [list-item]]
   [reitit.frontend.easy :as rfe]))

(defn home []
  (re-frame/dispatch [:clojure-tutorials.frontend.events/load-articles])
  (fn []
    (let [articles @(re-frame/subscribe [:clojure-tutorials.frontend.subs/sorted-articles])]
      (prn"aaa" articles)
      (if (= (ffirst articles) :loading?)
        [backdrop {:open true}
         [circular-progress ]]
        [list
         (doall
          (for [[id {:keys [title]}] articles
                :let [_ (prn "jaaa" title) 
                      html-tags (re-seq #"<[^>]*>" (str title))]
                :when (or (empty? html-tags) (every? #{"<code>" "</code>"} html-tags))]
            [list-item {:key id}
             [link {:href (str "article/" id) ;(rfe/href :clojure-tutorials.frontend.routes/article {:id id})
                    :color :inherit
                    :underline :hover}
              [:div {:dangerouslySetInnerHTML {:__html title}}]]]))]))))