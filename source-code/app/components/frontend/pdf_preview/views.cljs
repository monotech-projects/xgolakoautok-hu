
(ns app.components.frontend.pdf-preview.views
    (:require [random.api :as random]))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn- pdf-preview
  ; @param (keyword) preview-id
  ; @param (map) preview-props
  ;  {:height (string)
  ;   :src (string)
  ;   :width (string)}
  [_ {:keys [height src width]}]
  [:iframe {:src src :title "Preview" :style {:height height :width width :border-radius "var( --border-radius-m )"}}])

(defn component
  ; @param (keyword)(opt) preview-id
  ; @param (map) preview-props
  ;  {:height (string)
  ;   :src (string)
  ;   :width (string)}
  ;
  ; @usage
  ;  [pdf-preview {...}]
  ;
  ; @usage
  ;  [pdf-preview :my-pdf-preview {...}]
  ([preview-props]
   [component (random/generate-keyword) preview-props])

  ([preview-id preview-props]
   (let [] ;preview-props (pdf-preview.prototypes/preview-props-prototype preview-props)
        [pdf-preview preview-id preview-props])))
