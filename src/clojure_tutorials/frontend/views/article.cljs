(ns clojure-tutorials.frontend.views.article
  (:require
   [re-frame.core :as re-frame]
   [reagent-mui.material.typography :refer [typography]]
   [reagent.core :as r]))

(defn- set-inner-html [{:keys [desc title]}]
  (let [article-html (js/document.getElementById "article-content")]
    (when article-html
      (set! (.-innerHTML article-html) desc)
      (set! (.-innerHTML (js/document.getElementById "article-title")) title))))

(defn article [article-item]
  #_(r/create-class
     {:reagent-render (fn [article-item]
                        (set-inner-html article-item)
                        [:div {:id (str "article_" @(re-frame/subscribe [:clojure-tutorials.frontend.subs/article-id]))}
                         [typography {:variant :h4 :id "article-title"} "Loading..."]
                         [:div#article-content "Loading..."]])

      ;; Use after-render to handle side-effects after the component is rendered
      :component-did-mount (fn []
                             ;; After the component is mounted, trigger the update for innerHTML
                             (r/after-render #(set-inner-html article-item)))}))