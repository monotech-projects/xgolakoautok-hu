
(ns app.price-quotes.backend.editor.mutations
    (:require [app.common.backend.api                     :as common]
              [app.price-quotes.backend.editor.prototypes :as editor.prototypes]
              [app.price-quotes.backend.handler.helpers   :as handler.helpers]
              [candy.api                                  :refer [return]]
              [com.wsscode.pathom3.connect.operation      :as pathom.co :refer [defmutation]]
              [mongo-db.api                               :as mongo-db]
              [pathom.api                                 :as pathom]
              [x.user.api                                 :as x.user]))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn add-item-f
  ; @param (map) env
  ;  {:request (map)}
  ; @param (map) mutation-props
  ;  {:item (namespaced map)}
  ;
  ; @return (namespaced map)
  [{:keys [request]} {:keys [item]}]
  (if (x.user/request->authenticated? request)
      (letfn [(prepare-f [document] (->> document (common/added-document-prototype request)
                                                  (editor.prototypes/added-document-prototype)))]
             (when-let [item (mongo-db/save-document! "price_quotes" item {:prepare-f prepare-f})]
                       (handler.helpers/increase-annual-count!)
                       (return item)))))

(defmutation add-item!
             ; @param (map) env
             ; @param (map) mutation-props
             ;  {:item (namespaced map)}
             ;
             ; @return (namespaced map)
             [env mutation-props]
             {::pathom.co/op-name 'price-quotes.editor/add-item!}
             (add-item-f env mutation-props))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn save-item-f
  ; @param (map) env
  ;  {:request (map)}
  ; @param (map) mutation-props
  ;  {:item (namespaced map)}
  ;
  ; @return (namespaced map)
  [{:keys [request]} {:keys [item]}]
  (if (x.user/request->authenticated? request)
      (letfn [(prepare-f [document] (->> document (common/updated-document-prototype request)
                                                  (editor.prototypes/updated-document-prototype)))]
             (mongo-db/save-document! "price_quotes" item {:prepare-f prepare-f}))))

(defmutation save-item!
             ; @param (map) env
             ; @param (map) mutation-props
             ;  {:item (namespaced map)}
             ;
             ; @return (namespaced map)
             [env mutation-props]
             {::pathom.co/op-name 'price-quotes.editor/save-item!}
             (save-item-f env mutation-props))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

; @constant (functions in vector)
(def HANDLERS [add-item! save-item!])

(pathom/reg-handlers! ::handlers HANDLERS)
