(ns clojure-tutorials.backend.static-resources
  (:require
   [clojure.edn :as edn]
   [clojure.java.io :as io]
   [clojure.string :as str]
   [hiccup2.core :as h]
   [cheshire.core :as json])
  (:import (java.time ZonedDateTime)
           (java.time.format DateTimeFormatter)))

(defn- load-edn [source]
  (with-open [r (io/reader source)]
    (edn/read (java.io.PushbackReader. r))))

(defn save-file [path content & {:keys [append]}]
  (let [file (io/file path)]
    (io/make-parents file)
    (spit file content :append append)))

(defn fonts []
  #_'([:link {:rel "preconnect" :href "https://fonts.googleapis.com"}]
    [:link {:rel "preconnect" :href "https://fonts.gstatic.com" :crossorigin true}]
    [:link
     {:href
      "https://fonts.googleapis.com/css2?family=Roboto+Mono:ital,wght@0,100..700;1,100..700&family=Roboto:ital,wght@0,100;0,300;0,400;0,500;0,700;0,900;1,100;1,300;1,400;1,500;1,700;1,900&display=swap"
      :rel "stylesheet"}]))

(defn ads []
  '([:script
     {:async true
      :src
      "https://pagead2.googlesyndication.com/pagead/js/adsbygoogle.js?client=ca-pub-4545173212980791"
      :crossorigin "anonymous"}]))

(defn strip-html-tags [html]
  (-> html
      (str/replace #"<[^>]*?>" "")
      (str/replace #" " " ")
      (str/trim)))

(defn footer []
  [:footer.footer
   [:div.footer-content
    [:ul.footer-links
     [:li
      [:a.footer-link {:href "/politica-de-cookies"}
       "Política de Cookies"]]
     [:li
      [:a.footer-link {:href "/politica-de-privacidad"}
       "Política de Privacidad"]]
     [:li
      [:a.footer-link {:href "/sobre-nosotros"}
       "Sobre Nosotros"]]]
    [:div.footer-note.deaccept-cookies
     [:button#unaccept-cookies
      "Deshacer aceptación de cookies"]]]
   [:p.footer-note "Última actualización: 16 de diciembre de 2024"]])

(defn index []
  (let [content (h/html
                 (h/raw "<!DOCTYPE html>")
                 [:html {:lang "en"}
                  [:head
                   [:meta {:charset "utf-8"}]
                   [:meta {:name "description" :content "Tutoriales para Desarrolladores en Clojure: Guías, Ejemplos y Buenas Prácticas"}]
                   [:meta {:name "viewport" :content "initial-scale=1, width=device-width"}]
                   [:title "Tutoriales Clojure"]
                   [:link {:rel "stylesheet" :href "styles.css"}]
                   (fonts)
                   [:meta {:name "google-site-verification"
                           :content "NEdWFaLiY4F7Mnw5exM0mj9XLjyr9mJ5VFtGVqjBeWA"}]
                   [:script {:type "application/ld+json"}
                    (h/raw (json/generate-string
                            {"@context" "https://schema.org"
                             "@type" "ItemList"
                             :itemListElement (map-indexed (fn [idx article]
                                                             {"@type" "ListItem"
                                                              :position (inc idx)
                                                              :name (strip-html-tags (:title article))})
                                                           (load-edn "resources/public/articles.edn"))}))]
                   [:meta {:name "google-adsense-account" :content "ca-pub-4545173212980791"}]]
                  (ads)
                  [:body
                   [:div#app]
                   [:main.index
                    [:h1 "Tutoriales de Clojure: aprende conceptos clave y mejores prácticas"]
                    [:div.article-list
                     (for [{:keys [title desc id]} (load-edn "resources/public/articles.edn")]
                       [:a.card-link {:href (format "article/%s" id)
                                      :aria-label (format "Leer más sobre %s" (str/lower-case title))
                                      :id (format "clojure-article-%s" id)}
                        [:div.card
                         [:div.card-content
                          [:h2.card-title title]
                          [:div.card-description (h/raw (first (str/split desc #"\n")))]
                          [:p.read-more
                           "Leer más..."]]]])]]
                   (footer)
                   [:script {:src "/js/compiled/app.js" :defer true}]]])]
    (save-file "resources/public/index.html" content)))

(defn articles []
  (doseq [{:keys [title desc id]} (filter (comp #{151 152 115} :id) (load-edn "resources/public/articles.edn"))]
    (let [path (str "resources/public/article/" id "/index.html")
          content (h/html
                   (h/raw "<!DOCTYPE html>")
                   [:html {:lang "es"}
                    [:head
                     [:meta {:charset "utf-8"}]
                     [:title (str/replace title #"<[^>]*>" "")]
                     [:meta {:name "description" :content (strip-html-tags (first (str/split desc #"\n")))}] 
                     [:link {:rel "stylesheet" :href "/../../styles.css"}]
                     [:meta {:name "viewport" :content "initial-scale=1, width=device-width"}]
                     (fonts)
                     [:meta {:name "google-site-verification"
                             :content "NEdWFaLiY4F7Mnw5exM0mj9XLjyr9mJ5VFtGVqjBeWA"}]
                     [:script {:type "application/ld+json"}
                      (h/raw (json/generate-string
                              {"@context" "https://schema.org"
                               "@type" "Article"
                               :headline (strip-html-tags title)
                               :description (strip-html-tags desc)}))]
                     [:meta {:name "google-adsense-account" :content "ca-pub-4545173212980791"}]]
                    (ads)]
                   [:body
                    [:div#app]
                    [:main
                     [:article
                      [:h1 (h/raw title)]
                      (h/raw desc)]]
                    (footer)
                    [:script {:src "/js/compiled/app.js" :defer true}]])]
      (save-file path content))))

(defn sitemap []
  (let [formatter (DateTimeFormatter/ofPattern "yyyy-MM-dd'T'HH:mm:ssXXX")
        now (.format (ZonedDateTime/now java.time.ZoneOffset/UTC) formatter)]
    (save-file
     "resources/public/sitemap.xml"
     (h/html
      (h/raw "<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
      [:urlset
       {:xmlns "http://www.sitemaps.org/schemas/sitemap/0.9"
        :xmlns:xsi "http://www.w3.org/2001/XMLSchema-instance"
        :xsi:schemalocation
        "http://www.sitemaps.org/schemas/sitemap/0.9 http://www.sitemaps.org/schemas/sitemap/0.9/sitemap.xsd"}
       [:url
        [:loc "https://solucionesclojure.com/"]
        [:lastmod now]]
       (for [{:keys [id]} (load-edn "resources/public/articles.edn")]
         [:url
          [:loc (format "https://solucionesclojure.com/article/%s" id)]
          [:lastmod now]])
       [:url
        [:loc "https://solucionesclojure.com/politica-de-privacidad"]
        [:lastmod now]]
       [:url
        [:loc "https://solucionesclojure.com/politica-de-cookies"]
        [:lastmod now]]
       [:url
        [:loc "https://solucionesclojure.com/sobre-nosotros"]
        [:lastmod now]]]))))

(defn rgpd []
  (save-file
   "resources/public/politica-de-privacidad/index.html"
   (h/html
    (h/raw "<!DOCTYPE html>")
    [:html {:lang "es"}
     [:head
      [:meta {:charset "UTF-8"}]
      [:meta {:name "viewport" :content "width=device-width, initial-scale=1.0"}]
      [:meta
       {:name "description"
        :content
        "Política de privacidad del blog de tutoriales de Clojure. Cumplimos con el RGPD y explicamos cómo manejamos tus datos personales."}]
      [:title "Política de Privacidad"]
      [:link {:rel "stylesheet" :href "/../../styles.css"}]
      (fonts)]
     [:body
      [:div#app]
      [:main.index
       [:h1 "Política de Privacidad"]
       [:section
        [:h2 "¿Quiénes somos?"]
        [:p
         "Este sitio web es un blog dedicado a compartir tutoriales y soluciones sobre el lenguaje de programación Clojure. Nuestro objetivo es proporcionar contenido educativo sin recopilar datos personales identificables de los usuarios."]]
       [:section
        [:h2 "Datos que recopilamos"]
        [:p
         "No recopilamos datos personales directamente. Sin embargo, utilizamos Google AdSense para mostrar anuncios, lo que podría implicar la recopilación de datos por parte de Google."]]
       [:section
        [:h2 "Google AdSense"]
        [:p
         "Este sitio utiliza Google AdSense para monetizar el contenido mediante anuncios. Google, como proveedor externo, utiliza cookies para personalizar los anuncios en función de las visitas de los usuarios a este y otros sitios web."]
        [:p [:strong "Cómo funciona:"]]
        [:ul
         [:li
          "Google utiliza cookies para mostrar anuncios relevantes basados en los intereses del usuario."]
         [:li
          "Los usuarios pueden optar por desactivar las cookies de personalización de anuncios visitando la página de "
          [:a {:href "https://www.google.com/settings/ads" :target "_blank"}
           "Configuración de Anuncios de Google"]
          "."]
         [:li
          "Para más información sobre cómo Google gestiona los datos, consulta su "
          [:a {:href "https://policies.google.com/technologies/ads"
               :target "_blank"}
           "Política de Privacidad"]
          "."]]]
       [:section
        [:h2 "Cookies"]
        [:p
         "Este sitio utiliza cookies de Google AdSense para personalizar los anuncios. Estas cookies no contienen información personal identificable."]
        [:p
         "Si no deseas que estas cookies se almacenen en tu dispositivo, puedes configurar tu navegador para bloquearlas o eliminarlas. Consulta la sección de ayuda de tu navegador para obtener más información."]]
       [:section
        [:h2 "Enlaces a sitios de terceros"]
        [:p
         "Este sitio puede contener enlaces a sitios externos. No somos responsables de las prácticas de privacidad ni del contenido de dichos sitios. Te recomendamos que leas sus políticas de privacidad antes de proporcionar cualquier información personal."]]
       [:section
        [:h2 "Tus derechos según el RGPD"]
        [:p "Como usuario de la Unión Europea, tienes derecho a:"]
        [:ul
         [:li "Acceder a los datos personales que tengamos sobre ti (si aplica)."]
         [:li "Solicitar la rectificación o eliminación de tus datos personales."]
         [:li "Restringir u oponerte al procesamiento de tus datos personales."]
         [:li
          "Presentar una queja ante la autoridad de protección de datos de tu país."]]
        [:p
         "Para ejercer estos derechos, puedes ponerte en contacto con nosotros a través del correo electrónico indicado en la página de contacto."]]
       [:section
        [:h2 "Modificaciones a esta Política de Privacidad"]
        [:p
         "Nos reservamos el derecho de actualizar esta política de privacidad en cualquier momento. Cualquier cambio será publicado en esta página con la fecha de última actualización."]]
       [:section
        [:h2 "Contacto"]
        [:p
         "Si tienes alguna pregunta o inquietud sobre esta Política de Privacidad, puedes ponerte en contacto con nosotros a través del correo electrónico indicado en la página de contacto."]]]
      (footer)
      [:script {:src "/js/compiled/app.js" :defer true}]]])))

(defn cookies []
  (save-file
   "resources/public/politica-de-cookies/index.html"
   (h/html
    (h/raw "<!DOCTYPE html>")
    [:html {:lang "es"}
     [:head
      [:meta {:charset "UTF-8"}]
      [:meta {:name "viewport" :content "width=device-width, initial-scale=1.0"}]
      [:meta
       {:name "description"
        :content
        "Política de Cookies para el blog de tutoriales de Clojure. Explicamos qué cookies utilizamos y cómo gestionarlas."}]
      [:title "Política de Cookies"]
      [:link {:rel "stylesheet" :href "/../../styles.css"}]
      (fonts)]
     [:body
      [:div#app]
      [:main.index
       [:h1 "Política de Cookies"]
       [:section
        [:h2 "¿Qué son las cookies?"]
        [:p
         "Las cookies son pequeños archivos de texto que se almacenan en tu dispositivo cuando visitas un sitio web. Se utilizan para recordar información sobre ti, como tus preferencias o tu historial de navegación."]]
       [:section
        [:h2 "¿Qué tipos de cookies utilizamos?"]
        [:p
         "En este sitio web utilizamos cookies para mostrar anuncios a través de Google AdSense. Estas cookies se clasifican como:"]
        [:ul
         [:li
          [:strong "Cookies esenciales:"]
          "Estas cookies son necesarias para el funcionamiento del sitio web y no pueden desactivarse."]
         [:li
          [:strong "Cookies de personalización de anuncios:"]
          "Google AdSense utiliza cookies para mostrar anuncios relevantes basados en tus intereses y tu historial de navegación."]]]
       [:section
        [:h2 "Cookies de Google AdSense"]
        [:p
         "Google utiliza cookies para personalizar los anuncios y medir su rendimiento. Para obtener más información sobre cómo Google gestiona las cookies, consulta su "
         [:a {:href "https://policies.google.com/technologies/ads" :target "_blank"}
          "Política de Cookies"]
         "."]]
       [:section
        [:h2 "¿Cómo gestionar las cookies?"]
        [:p
         "Puedes gestionar o desactivar las cookies en tu navegador web. A continuación, te proporcionamos enlaces a las instrucciones para los navegadores más populares:"]
        [:ul
         [:li
          [:a {:href "https://support.google.com/chrome/answer/95647?hl=es"
               :target "_blank"}
           "Google Chrome"]]
         [:li
          [:a {:href "https://support.mozilla.org/es/kb/Eliminar%20cookies"
               :target "_blank"}
           "Mozilla Firefox"]]
         [:li
          [:a
           {:href
            "https://support.microsoft.com/es-es/help/17442/windows-internet-explorer-delete-manage-cookies"
            :target "_blank"}
           "Internet Explorer"]]
         [:li
          [:a {:href "https://support.apple.com/es-es/guide/safari/sfri11471/mac"
               :target "_blank"}
           "Safari"]]]
        [:p
         "Ten en cuenta que desactivar las cookies puede afectar el funcionamiento de algunas características del sitio web."]]
       [:section
        [:h2 "Consentimiento"]
        [:p
         "Al continuar navegando en este sitio web, aceptas el uso de cookies según lo descrito en esta Política de Cookies."]]]
      (footer)
      [:script {:src "/js/compiled/app.js" :defer true}]]])))

(defn about-us []
  (save-file
   "resources/public/sobre-nosotros/index.html"
   (h/html
    (h/raw "<!DOCTYPE html>")
    [:html {:lang "es"}
     [:head
      [:meta {:charset "UTF-8"}]
      [:meta {:name "viewport" :content "width=device-width, initial-scale=1.0"}]
      [:meta
       {:name "description"
        :content
        "Tutoriales de Clojure para aprender conceptos clave y mejores prácticas en programación funcional."}]
      [:title "Tutoriales de Clojure: Aprende conceptos clave y mejores prácticas"]
      [:link {:rel "stylesheet" :href "/../../styles.css"}]
      (fonts)]
     [:body
      [:div#app]
      [:main.index
       [:h1 "Tutoriales de Clojure: Aprende conceptos clave y mejores prácticas"]
       [:section.about-us
        [:h2 "Sobre Nosotros"]
        [:p
         "Bienvenidos a nuestro sitio de"
         [:strong "Tutoriales de Clojure"]
         " , el espacio ideal para aprender los conceptos clave y las mejores prácticas del lenguaje de programación funcional Clojure. Clojure es un lenguaje poderoso que se destaca por su simplicidad, inmutabilidad y enfoque funcional, lo que lo convierte en una herramienta increíblemente eficaz para la programación concurrente y el procesamiento de datos."]
        [:p
         "Nuestra misión es proporcionar recursos educativos accesibles y de alta calidad para desarrolladores que desean dominar Clojure. Ya sea que seas un principiante buscando los primeros pasos o un desarrollador experimentado en busca de nuevas técnicas y optimizaciones, tenemos el contenido adecuado para ti. Nos apasiona ayudar a los programadores a dominar Clojure y aplicar sus poderosas características en sus proyectos profesionales."]
        [:p
         "En nuestro sitio encontrarás una variedad de tutoriales prácticos que cubren desde los conceptos básicos hasta las técnicas más avanzadas. Cada artículo está diseñado para guiarte paso a paso, proporcionando ejemplos claros y explicaciones detalladas que te permitirán comprender mejor cómo trabajar con Clojure en situaciones del mundo real. Además, incluimos ejercicios prácticos y soluciones a problemas comunes para reforzar tu aprendizaje."]
        [:p
         "El enfoque de Clojure en la inmutabilidad y el manejo eficiente de colecciones lo convierte en un lenguaje ideal para desarrollar aplicaciones robustas y escalables. A través de nuestros tutoriales, buscamos brindarte las herramientas necesarias para escribir código más limpio, eficiente y fácil de mantener, mientras exploras las características únicas de este lenguaje funcional."]
        [:p
         "Creemos en el aprendizaje continuo y en compartir el conocimiento. Por eso, estamos comprometidos a mantener nuestro contenido actualizado con las últimas versiones de Clojure, así como con nuevas técnicas y mejores prácticas en el desarrollo de software. Nuestro objetivo es construir una comunidad activa de desarrolladores que no solo aprendan, sino que también contribuyan con sus conocimientos y experiencias."]
        [:p
         "Gracias por visitar nuestro sitio. ¡Esperamos que disfrutes de los tutoriales y que te ayuden a avanzar en tu dominio de Clojure!"]]]
      (footer)]]
    [:link {:as "script" :href "/js/compiled/app.js" :rel "preload"}]
    [:script {:src "/js/compiled/app.js" :defer true}])))

(comment
  (h/html  [:a 1])
  (do (index)
      (articles)
      (rgpd)
      (cookies)
      (sitemap)
      (about-us))
  
  (save-file "resources/prueba/delete" "aa" :append true)
  (json/generate-string {"@context" "https://schema.org"
                         "@type" "ItemList"})
  (io/delete-file "/resources/public/article") 

(count (load-edn "resources/public/articles.edn"))

  (->> (map :id (load-edn "resources/public/articles.edn"))
       count)

  (index)
  (articles)
  (json/generate-string {:fecha (java.util.Date.)} {:date-format "yyyy-MM-dd"})


  (json/parse-string "{\"nombre\":\"Juan\",\"edad\":30}" true)
  
  (filter (comp #{152} :id) (load-edn "resources/public/articles.edn"))
  
  (let [formatter (DateTimeFormatter/ofPattern "yyyy-MM-dd'T'HH:mm:ssXXX")
        now (.format (ZonedDateTime/now java.time.ZoneOffset/UTC) formatter)]
    (save-file
     "resources/public/sitemap.xml"
     (h/html
      (for [{:keys [id]} (load-edn "resources/public/articles.edn")
            :when (#{115 151 152} id)]
        [:url
         [:loc (format "https://solucionesclojure.com/article/%s" id)]
         [:lastmod now]]))
     :append true)))