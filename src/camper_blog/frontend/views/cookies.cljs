(ns camper-blog.frontend.views.cookies
  (:require
   [re-frame.core :as re-frame]
   [reagent-mui.icons.warning :refer [warning]]
   [reagent-mui.material.button :refer [button]]
   [reagent-mui.material.dialog :refer [dialog]]
   [reagent-mui.material.dialog-actions :refer [dialog-actions]]
   [reagent-mui.material.dialog-content :refer [dialog-content]]
   [reagent-mui.material.dialog-title :refer [dialog-title]]
   [reagent-mui.material.link :refer [link]]
   [reagent-mui.material.snackbar :refer [snackbar]]
   [reagent-mui.styles :as styles]
   [reagent.core :as r]))

(defonce uncappet-opened? (r/atom false))

(defonce accepted? (r/atom (= (js/localStorage.getItem "cookie-rgpd-consent")
                              "accepted")))
(def styled-unnacept-dialog
  (styles/styled
   dialog
   (fn [{{:keys [spacing]} :theme}]
     {"& .MuiDialogTitle-root" {:display "flex"
                                :gap (spacing 1)
                                :align-items "center"}})))
(defn unnacept-dialog []
  [styled-unnacept-dialog
   {:open @uncappet-opened?
    :aria-labelledby "cookie-rgpd-unnacept-alert-dialog-title"
    :max-width "md"
    :on-close #(reset! uncappet-opened? false)}
   [dialog-title {:id "cookie-rgpd-unnacept-alert-dialog-title"}
    [warning {:color :error}]
    "Aceptación de cookies y RGPD"]
   [dialog-content
    "Usamos cookies para mejorar tu experiencia en nuestro sitio web. Al navegar en este sitio, aceptas el uso de cookies y nos das tu consentimiento para el tratamiento de tus datos personales, de acuerdo con la legislación vigente, como el RGPD. Para más información, consulta nuestra "
    [link {:href "/politica-de-cookies"
           :color :inherit}
     "Política de Cookies"]
    " y nuestra "
    [link {:href "/politica-de-cookies"
           :color :inherit}
     "Política de Privacidad"]
    "."]
   [dialog-actions
    [button {:on-click (fn []
                         (reset! uncappet-opened? false)
                         (js/localStorage.removeItem "cookie-rgpd-consent"))
             :color :inherit
             :variant :outlined} "Cerrar"]
    [button {:on-click (fn []
                         (reset! uncappet-opened? false)
                         (reset! accepted? false)
                         (js/localStorage.removeItem "cookie-rgpd-consent"))
             :color :error
             :variant :contained} "Deshacer aceptación"]]])

(defn unaccept-cookies []
  (reset! uncappet-opened? true))

(defn cookies-dialogs []
  [snackbar
   {:open (boolean (not (or @accepted?
                            (#{["politica-de-cookies"]
                               ["politica-de-privacidad"]
                               ["sobre-nosotros"]} @(re-frame/subscribe [:camper-blog.frontend.subs/route])))))
    :message (r/as-element
              [:<>
               "Utilizamos cookies para asegurarnos de ofrecerte la mejor experiencia en nuestro sitio web. Si continúas navegando en este sitio, asumiremos que estás de acuerdo con ello. Además, al aceptar, nos das tu consentimiento para el tratamiento de tus datos personales de acuerdo con el Reglamento General de Protección de Datos (RGPD)."
               [:br]
               [:br]
               "Para más información, consulta nuestra "
               [link {:href "/politica-de-cookies"
                      :target "_blank"
                      :color "inherit"}
                "Política de Cookies"]
               " y nuestra "
               [link {:href "/politica-de-privacidad"
                      :target "_blank"
                      :color "inherit"}
                "Política de Privacidad"]
               "."])
    :action (r/as-element [button {:on-click (fn []
                                               (reset! accepted? true)
                                               (js/localStorage.setItem "cookie-rgpd-consent" "accepted"))
                                   :variant :contained} "Aceptar y continuar"])}])