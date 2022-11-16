
(ns app.services.frontend.preview.prototypes
    (:require [candy.api         :refer [param]]
              [mid-fruits.vector :as vector]))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn preview-props-prototype
  ; @param (keyword) preview-id
  ; @param (map) preview-props
  ;  {:items (namespaced maps in vector)}
  ;
  ; @return (map)
  ;  {:items (namespaced maps in vector)}
  [_ {:keys [items] :as preview-props}]
  (merge {}
         (param preview-props)
         {:items (vector/remove-items-by items nil?)}))