(ns camper-blog.backend.static-resources
  (:require
   [clojure.edn :as edn]
   [clojure.java.io :as io]
   [clojure.string :as str]
   [hiccup2.core :as h]
   [cheshire.core :as json])
  (:import (java.time ZonedDateTime)
           (java.time.format DateTimeFormatter)))

(def domain "https://campersyfurgonets.com")

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
        :rel "stylesheet"}])
  '())

(defn ads []
  #_'([:script
       {:async true
        :src
        "https://pagead2.googlesyndication.com/pagead/js/adsbygoogle.js?client=ca-pub-4545173212980791"
        :crossorigin "anonymous"}]
      [:meta {:name "google-adsense-account" :content "ca-pub-4545173212980791"}])
  '())

(defn common-head-tags []
  '([:meta {:charset "utf-8"}]
    [:meta {:name "viewport" :content "initial-scale=1, width=device-width"}]
    [:link {:rel "stylesheet" :href "/styles.css"}]
    ;(fonts)
    ;(ads)
    ))

(defn deaccent [str]
  (str/replace
   (java.text.Normalizer/normalize str java.text.Normalizer$Form/NFD)
   #"\p{InCombiningDiacriticalMarks}+" ""))

(defn sanitize-title-for-pathname [title]
  (-> title
      str/lower-case
      deaccent
      (str/replace #"[^\w\s]" "")
      (str/replace #"\s+" "-")))

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
                   [:meta {:name "description" :content "Todo sobre furgonetas y el mundo camper"}]
                   [:meta {:name "viewport" :content "initial-scale=1, width=device-width"}]
                   [:title "Furgonetas camper: guías, consejos y destinos"]
                   (common-head-tags)
                   [:script {:type "application/ld+json"}
                    (h/raw (json/generate-string
                            {"@context" "https://schema.org"
                             "@type" "ItemList"
                             :itemListElement (map-indexed (fn [idx {:keys [title description]}]
                                                             (let [entrypage (sanitize-title-for-pathname title)]
                                                               {"@type" "ListItem",
                                                                "position" (inc idx)
                                                                "url" (format "%s/%s" domain entrypage)
                                                                "name" title,
                                                                "description" description,
                                                                "mainEntityOfPage" {"@type" "WebPage"
                                                                                    "@id" entrypage}}))

                                                           (load-edn "resources/public/articles.edn"))}))]]
                  [:body
                   [:div#app]
                   [:main.index
                    [:h1
                     "Todo lo que necesitas saber sobre furgonetas camper: Guías, consejos y destinos"]
                    [:div.article-list
                     (for [{:keys [title description id]} (load-edn "resources/public/articles.edn")]
                       [:a.card-link {:href (sanitize-title-for-pathname title)
                                      :aria-label (format "Leer más sobre %s" (str/lower-case title))
                                      :id (format "clojure-article-%s" id)}
                        [:div.card
                         [:div.card-content
                          [:h2.card-title title]
                          [:div.card-description description]
                          [:p.read-more
                           "Leer más..."]]]])]]
                   (footer)
                   [:script {:src "/js/compiled/app.js" :defer true}]]])]
    (save-file "resources/public/index.html" content)))

(defn articles []
  (doseq [{:keys [title description main modified-at created-at keywords]}
          (load-edn "resources/public/articles.edn")]
    (let [path (format "resources/public/%s/index.html" (sanitize-title-for-pathname title))
          content (h/html
                   (h/raw "<!DOCTYPE html>")
                   [:html {:lang "es"}
                    [:head
                     (common-head-tags)
                     [:title title]
                     (when (seq keywords)
                       [:meta {:name "keywords" :content (str/join ", " keywords)}])
                     [:meta {:name "description" :content description}]
                     [:script {:type "application/ld+json"}
                      (h/raw (json/generate-string
                              {"@context" "https://schema.org",
                               "dateModified" modified-at
                               "author" {"@type" "Person" "name" "Jon Ramos"},
                               "mainEntityOfPage" {"@type" "WebPage", "@id" "https://yourwebsite.com/furgonetas-camper"},
                               "articleBody"
                               "<section><h1>Furgoneta camper: Las mejores opciones para viajes por carretera</h1><h2>1. Volkswagen California</h2><p>La <strong>Volkswagen California</strong> es un clásico en el mundo de las campers. Con varias generaciones en su haber, sigue siendo una opción favorita gracias a su diseño compacto y funcional. Es ideal para:</p><ul><li>Viajeros en pareja o pequeños grupos.</li><li>Escapadas de fin de semana o vacaciones cortas.</li><li>Maniobrar fácilmente en carreteras estrechas o urbanas.</li></ul><p>La California destaca por su techo elevable, cocina integrada y múltiples configuraciones de asientos.</p><h2>2. Mercedes Marco Polo</h2><p>La <strong>Mercedes Marco Polo</strong> combina lujo y funcionalidad. Es perfecta para quienes buscan comodidad en sus aventuras. Algunas de sus características principales son:</p><ul><li>Interior de alta calidad con materiales premium.</li><li>Sistema de infoentretenimiento avanzado.</li><li>Opciones de personalización según tus necesidades.</li></ul><p>La Marco Polo es ideal para quienes desean viajar con estilo y comodidad.</p><h2>3. Fiat Ducato Camper</h2><p>La <strong>Fiat Ducato Camper</strong> es una opción espaciosa y versátil. Es popular entre familias y viajeros que necesitan más espacio para equipamiento o comodidad. Sus ventajas incluyen:</p><ul><li>Gran capacidad de almacenamiento.</li><li>Posibilidad de añadir baño y ducha.</li><li>Consumo de combustible eficiente para su tamaño.</li></ul><p>Es una de las furgonetas más utilizadas para proyectos de camperización por su amplio interior y flexibilidad.</p><h2>4. Ford Transit Custom Nugget</h2><p>La <strong>Ford Transit Custom Nugget</strong> es una alternativa económica pero funcional. Perfecta para aquellos que buscan calidad sin un precio elevado. Algunas características destacadas son:</p><ul><li>Cocina trasera completa con fregadero y fogones.</li><li>Zonas separadas para dormir y estar.</li><li>Techo elevable para mayor comodidad.</li></ul><p>La Transit Custom Nugget es ideal para principiantes en el mundo camper.</p><h2>5. Renault Trafic SpaceNomad</h2><p>La <strong>Renault Trafic SpaceNomad</strong> es perfecta para viajeros prácticos y aventureros. Destaca por:</p><ul><li>Diseño compacto y fácil de manejar.</li><li>Zona de cocina integrada con almacenamiento eficiente.</li><li>Buena relación calidad-precio.</li></ul><p>Es una excelente opción para viajes por carretera y escapadas frecuentes.</p><p>Estas opciones representan lo mejor en el mundo de las furgonetas camper. Analiza tus necesidades y elige la que más se adapte a tus viajes por carretera.</p></section>",
                               "publisher"
                               {"@type" "Person",
                                "name" "Jon Ramos"},
                               "datePublished" created-at,
                               "@type" "Article",
                               "description" description,
                               "headline" title}))]]]
                   [:body
                    [:div#app]
                    [:main
                     [:article
                      [:h1 title]
                      [:h2.description description]
                      (h/raw main)]]
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
        [:loc domain]
        [:lastmod now]]
       (for [{:keys [title modified-at]} (load-edn "resources/public/articles.edn")]
         [:url
          [:loc (format "%s/%s" domain (sanitize-title-for-pathname title))]
          [:lastmod modified-at]])
       [:url
        [:loc (format "%s/politica-de-privacidad" domain)]
        [:lastmod now]]
       [:url
        [:loc (format "%s/politica-de-cookies" domain)]
        [:lastmod now]]
       [:url
        [:loc (format "%s/sobre-nosotros" domain)]
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
        "Política de privacidad del blog de campersyfurgonets.com. Cumplimos con el RGPD y explicamos cómo manejamos tus datos personales."}]
      [:title "Política de Privacidad"]
      [:link {:rel "stylesheet" :href "/styles.css"}]
      (fonts)]
     [:body
      [:div#app]
      [:main.index
       [:h1 "Política de Privacidad y Protección de Datos Personales"]
       [:p
        "En "
        [:strong "campersyfurgonets.com"]
        " respetamos y protegemos la privacidad de nuestros usuarios. Esta política de privacidad explica cómo recopilamos, usamos, almacenamos y compartimos tu información personal de acuerdo con el Reglamento General de Protección de Datos (RGPD) de la Unión Europea."]
       [:section
        [:h2 "1. Responsable del tratamiento de datos"]
        [:p
         "El responsable del tratamiento de los datos personales recogidos a través de este sitio web es"
         [:strong "Nombre de tu empresa o tu nombre"]
         ", con domicilio en"
         [:strong "dirección"]
         ", y con correo electrónico para contacto"
         [:strong "correo@tudominio.com"]
         "."]]
       [:section
        [:h2 "2. Datos personales que recogemos"]
        [:p
         "Recopilamos información personal que nos proporcionas directamente, como tu nombre, dirección de correo electrónico y cualquier otra información proporcionada a través de formularios de contacto o suscripción. Además, podemos recopilar datos de navegación, como la dirección IP, el tipo de navegador y la fecha y hora de acceso mediante el uso de cookies y tecnologías similares."]]
       [:section
        [:h2 "3. Uso de los datos personales"]
        [:p
         "Los datos personales que recopilamos se utilizan para los siguientes fines:"]
        [:ul
         [:li
          "Para proporcionarte contenido y servicios de calidad relacionados con el blog."]
         [:li
          "Para responder a consultas o solicitudes a través de formularios de contacto."]
         [:li
          "Para enviarte comunicaciones de marketing, si has dado tu consentimiento."]
         [:li
          "Para personalizar la experiencia en el sitio web, incluido el uso de anuncios."]]]
       [:section
        [:h2 "4. Uso de cookies y publicidad"]
        [:p
         "Este sitio web utiliza cookies para mejorar la experiencia del usuario, personalizar el contenido y los anuncios, y analizar el tráfico del sitio. En particular, usamos servicios de terceros, como Google AdSense, para mostrar anuncios personalizados. Estos servicios pueden recopilar información sobre tu actividad en línea para ofrecer anuncios relevantes según tus intereses."]
        [:p
         "Al usar este sitio web, aceptas el uso de cookies para estos fines. Si no estás de acuerdo, puedes cambiar la configuración de las cookies en tu navegador o utilizar herramientas de gestión de cookies para deshabilitar su uso. Ten en cuenta que esto puede afectar tu experiencia de navegación y la visualización de anuncios."]]
       [:section
        [:h2 "5. Consentimiento"]
        [:p
         "Al navegar por este sitio web y proporcionarnos tus datos personales, consientes el tratamiento de tus datos conforme a esta política de privacidad. Si deseas retirar tu consentimiento en cualquier momento, puedes hacerlo poniéndote en contacto con nosotros a través de los canales indicados en la sección 1."]]
       [:section
        [:h2 "6. Derechos del usuario"]
        [:p
         "Según el RGPD, tienes los siguientes derechos sobre tus datos personales:"]
        [:ul
         [:li
          [:strong "Derecho de acceso"]
          ": Puedes solicitar una copia de los datos personales que tenemos sobre ti."]
         [:li
          [:strong "Derecho de rectificación"]
          ": Puedes corregir cualquier dato personal incorrecto o incompleto."]
         [:li
          [:strong "Derecho de supresión"]
          ": Puedes solicitar la eliminación de tus datos personales."]
         [:li
          [:strong "Derecho a la limitación del tratamiento"]
          ": Puedes pedirnos que limitemos el tratamiento de tus datos."]
         [:li
          [:strong "Derecho a la portabilidad de los datos"]
          ": Puedes solicitar que te proporcionemos tus datos en un formato estructurado y de uso común."]
         [:li
          [:strong "Derecho a la oposición"]
          ": Puedes oponerte al tratamiento de tus datos personales para ciertos fines, como el marketing directo."]]
        [:p
         "Para ejercer cualquiera de estos derechos, puedes ponerte en contacto con nosotros a través de"
         [:strong "correo@tudominio.com"]
         "."]]
       [:section
        [:h2 "7. Seguridad de los datos"]
        [:p
         "Implementamos medidas de seguridad técnicas y organizativas para proteger tus datos personales contra el acceso no autorizado, alteración, divulgación o destrucción. Sin embargo, ten en cuenta que ningún sistema de transmisión de datos a través de Internet es completamente seguro."]]
       [:section
        [:h2 "8. Cambios en esta política de privacidad"]
        [:p
         "Nos reservamos el derecho de actualizar esta política de privacidad en cualquier momento. Te notificaremos cualquier cambio significativo a través de un aviso en el sitio web. Te recomendamos revisar periódicamente esta política para estar informado sobre cómo protegemos tus datos personales."]]]
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
        "Política de Cookies para campersyfurgonets.com. Explicamos qué cookies utilizamos y cómo gestionarlas."}]
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
         "Las cookies son pequeños archivos de texto que se almacenan en el dispositivo del usuario cuando visita un sitio web. Estas cookies permiten al sitio web recordar su actividad o preferencias a lo largo del tiempo."]]
       [:section
        [:h2 "¿Por qué utilizamos cookies?"]
        [:p
         "Utilizamos cookies para mejorar la experiencia del usuario, analizar el tráfico web y mostrar anuncios personalizados. Algunas cookies son necesarias para el funcionamiento del sitio, mientras que otras nos ayudan a ofrecer contenido y publicidad relevante."]]
       [:section
        [:h2 "Cookies de Google AdSense"]
        [:p
         "Google AdSense utiliza cookies para mostrar anuncios basados en el interés de los usuarios y en su actividad en otros sitios web. Estas cookies permiten personalizar los anuncios y medir la efectividad de las campañas publicitarias."]
        [:h3 "Tipos de cookies utilizadas por AdSense"]
        [:ul
         [:li
          [:strong "_gads"]
          ": Esta cookie se utiliza para la medición de anuncios y para servir anuncios basados en las visitas a sitios web anteriores."]
         [:li
          [:strong "_gac_" [:source_id]]
          ": Esta cookie está relacionada con la publicidad y es utilizada para hacer un seguimiento de la actividad de los usuarios, asociada con los clics en los anuncios y el rendimiento de la campaña."]
         [:li
          [:strong "IDE"]
          ": La cookie IDE es utilizada por Google DoubleClick para registrar y reportar las acciones del usuario después de ver o hacer clic en uno de los anuncios de un anunciante, con el fin de medir la eficacia de una campaña publicitaria."]]
        [:h3 "¿Cómo puedes gestionar o desactivar las cookies de Google AdSense?"]
        [:p
         "Puedes desactivar el uso de cookies para la personalización de anuncios en el "
         [:a {:href "https://adssettings.google.com/"
              :target "_blank"
              :rel "noopener noreferrer"}
          "administrador de configuración de anuncios de Google"]
         ". También puedes optar por desactivar las cookies en tu navegador, pero esto podría afectar la funcionalidad del sitio web y la experiencia de usuario en los anuncios."]]
       [:section
        [:h2 "Cookies de terceros"]
        [:p
         "Además de las cookies utilizadas por este sitio, terceros como Google AdSense, pueden utilizar cookies para ofrecer anuncios personalizados y analizar el tráfico web. Estos terceros pueden recoger información de forma anónima sobre tu actividad en la web."]]]
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
      [:title (format "Sobre Nosotros - %s" domain)]
      [:link {:rel "stylesheet" :href "/styles.css"}]
      (fonts)]
     [:body
      [:div#app]
      [:main.index
       [:section
        [:h2 "¿Quiénes somos?"]
        [:p
         "En " [:strong "campersyfurgonets"]
         " somos un equipo de entusiastas de los viajes por carretera y la vida en furgoneta. Nuestro objetivo es compartir nuestra pasión por las furgonetas camper, brindando a nuestros lectores una guía completa para transformar una furgoneta común en el hogar perfecto para aventuras sobre ruedas."]]
       [:section
        [:h2 "Nuestra misión"]
        [:p
         "Queremos inspirar y empoderar a todos aquellos que deseen experimentar la libertad de viajar en furgoneta. Ya sea que estés buscando convertir tu furgoneta en una camper o simplemente aprender sobre los mejores destinos para viajes por carretera, estamos aquí para proporcionarte la información más útil y actualizada."]]
       [:section
        [:h2 "Nuestra visión"]
        [:p "La visión de campersyfurgonets es convertirnos en la principal fuente de información y recursos para viajeros que buscan transformar su furgoneta en el vehículo perfecto para aventuras. Queremos ser la plataforma de referencia para todo lo relacionado con furgonetas camper: desde guías de conversión hasta consejos sobre destinos y rutas por carretera."]]
       [:section
        [:h2 "¿Qué hacemos?"]
        [:p
         "Nos dedicamos a proporcionar contenido detallado y útil sobre cómo convertir tu furgoneta en una camper, qué furgonetas son las mejores opciones para este propósito, qué accesorios necesitas, y consejos sobre cómo viajar cómodamente en tu furgoneta camper. Además, exploramos destinos recomendados, ofreciendo guías para viajar por Europa y más allá."]
        [:p
         "Desde guías paso a paso hasta recomendaciones para mantener tu furgoneta en perfecto estado, nuestro objetivo es cubrir todos los aspectos esenciales para aquellos que desean vivir o viajar en una furgoneta camper."]]
       [:section
        [:h2 "¿Por qué elegimos este tema?"]
        [:p
         "Nos apasiona la idea de vivir y viajar en furgoneta porque representa una forma única de explorar el mundo, vivir de manera sostenible y disfrutar de la libertad total. La vida en una furgoneta camper es una experiencia transformadora que nos conecta con la naturaleza, fomenta la independencia y nos permite vivir al ritmo de nuestros propios sueños y objetivos."]]]]
     (footer)]
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

  (save-file
   "resources/public/articles.edn"
   (vec (map-indexed
         (fn [index m]
           (assoc m :slug (sanitize-title-for-pathname (:title m))
                  :id (inc index)))
         (load-edn "resources/public/articles.edn"))))

  (json/parse-string "{
                       \"@context\": \"https://schema.org\",
                       \"@type\": \"Article\",
                       \"headline\": \"Furgoneta camper: Las mejores opciones para viajes por carretera\",
                       \"description\": \"Descubre las mejores furgonetas camper para disfrutar de tus viajes por carretera. Ideal para aventureros que buscan comodidad y funcionalidad.\",
                       \"mainEntityOfPage\": {
                         \"@type\": \"WebPage\",
                         \"@id\": \"https://yourwebsite.com/furgonetas-camper\"
                       },
                       \"author\": {
                         \"@type\": \"Organization\",
                         \"name\": \"Tu Empresa\"
                       },
                       \"datePublished\": \"2024-12-14\",
                       \"dateModified\": \"2024-12-14\",
                       \"publisher\": {
                         \"@type\": \"Organization\",
                         \"name\": \"Tu Empresa\",
                         \"logo\": {
                           \"@type\": \"ImageObject\",
                           \"url\": \"https://yourwebsite.com/logo.png\"
                         }
                       },
                       \"articleBody\": \"<section><h1>Furgoneta camper: Las mejores opciones para viajes por carretera</h1><h2>1. Volkswagen California</h2><p>La <strong>Volkswagen California</strong> es un clásico en el mundo de las campers. Con varias generaciones en su haber, sigue siendo una opción favorita gracias a su diseño compacto y funcional. Es ideal para:</p><ul><li>Viajeros en pareja o pequeños grupos.</li><li>Escapadas de fin de semana o vacaciones cortas.</li><li>Maniobrar fácilmente en carreteras estrechas o urbanas.</li></ul><p>La California destaca por su techo elevable, cocina integrada y múltiples configuraciones de asientos.</p><h2>2. Mercedes Marco Polo</h2><p>La <strong>Mercedes Marco Polo</strong> combina lujo y funcionalidad. Es perfecta para quienes buscan comodidad en sus aventuras. Algunas de sus características principales son:</p><ul><li>Interior de alta calidad con materiales premium.</li><li>Sistema de infoentretenimiento avanzado.</li><li>Opciones de personalización según tus necesidades.</li></ul><p>La Marco Polo es ideal para quienes desean viajar con estilo y comodidad.</p><h2>3. Fiat Ducato Camper</h2><p>La <strong>Fiat Ducato Camper</strong> es una opción espaciosa y versátil. Es popular entre familias y viajeros que necesitan más espacio para equipamiento o comodidad. Sus ventajas incluyen:</p><ul><li>Gran capacidad de almacenamiento.</li><li>Posibilidad de añadir baño y ducha.</li><li>Consumo de combustible eficiente para su tamaño.</li></ul><p>Es una de las furgonetas más utilizadas para proyectos de camperización por su amplio interior y flexibilidad.</p><h2>4. Ford Transit Custom Nugget</h2><p>La <strong>Ford Transit Custom Nugget</strong> es una alternativa económica pero funcional. Perfecta para aquellos que buscan calidad sin un precio elevado. Algunas características destacadas son:</p><ul><li>Cocina trasera completa con fregadero y fogones.</li><li>Zonas separadas para dormir y estar.</li><li>Techo elevable para mayor comodidad.</li></ul><p>La Transit Custom Nugget es ideal para principiantes en el mundo camper.</p><h2>5. Renault Trafic SpaceNomad</h2><p>La <strong>Renault Trafic SpaceNomad</strong> es perfecta para viajeros prácticos y aventureros. Destaca por:</p><ul><li>Diseño compacto y fácil de manejar.</li><li>Zona de cocina integrada con almacenamiento eficiente.</li><li>Buena relación calidad-precio.</li></ul><p>Es una excelente opción para viajes por carretera y escapadas frecuentes.</p><p>Estas opciones representan lo mejor en el mundo de las furgonetas camper. Analiza tus necesidades y elige la que más se adapte a tus viajes por carretera.</p></section>\"
                     }")

  (def formatter (DateTimeFormatter/ofPattern "yyyy-MM-dd'T'HH:mm:ssXXX"))
  (.format (ZonedDateTime/now java.time.ZoneOffset/UTC) formatter))