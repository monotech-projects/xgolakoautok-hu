
(ns site.xgo.transfer
    (:require [io.api        :as io]
              [map.api       :as map]
              [x.core.api    :as x.core]
              [mongo-db.api  :as mongo-db]
              [normalize.api :as normalize]))

(defn convert [key-fn data]
  (letfn [(vec->map [m v] (assoc m (key-fn v) (map/remove-namespace v)))]
         (reduce vec->map {} data)))

;; -----------------------------------------------------------------------------
;; -----------------------------------------------------------------------------

(defn transfer-website-content-f
  [request]
  (io/read-edn-file "environment/website-content.edn"))

(x.core/reg-transfer! ::transfer-website-content!
  {:data-f      transfer-website-content-f
   :target-path [:site :website-content]})

;; -----------------------------------------------------------------------------
;; -----------------------------------------------------------------------------

(defn transfer-website-config-f
  [request]
  (io/read-edn-file "environment/website-config.edn"))

(x.core/reg-transfer! ::transfer-website-config!
  {:data-f      transfer-website-config-f
   :target-path [:site :website-config]})


;; -----------------------------------------------------------------------------
;; -----------------------------------------------------------------------------

(def categories-projection
     {:projection #:category{:name        1
                             :order       1
                             :models      1
                             :description 1}})

(defn transfer-categories-f
  [_]
  (let [data (mongo-db/get-collection "vehicle_categories" categories-projection)]
    (convert #(-> % :category/name (normalize/clean-text "-+")) data)))
        

(x.core/reg-transfer! ::transfer-categories!
  {:data-f      transfer-categories-f
   :target-path [:site :categories]})

;; -----------------------------------------------------------------------------
;; -----------------------------------------------------------------------------

(def models-projection
    {:projection #:model{:name      1
                         :types     1
                         :thumbnail 1}})

(defn transfer-models-f
  [request]
  (let [data (mongo-db/get-collection "vehicle_models" models-projection)]
    (convert #(-> % :model/id) data)))

(x.core/reg-transfer! ::transfer-models!
  {:data-f      transfer-models-f
   :target-path [:site :models]})

;; -----------------------------------------------------------------------------
;; -----------------------------------------------------------------------------

(def types-projection
    {:projection #:type{:added-at    0
                        :added-by    0
                        :modified-at 0
                        :modified-by 0}})

(defn transfer-types-f
  [request]
  (let [data (mongo-db/get-collection "vehicle_types" types-projection)]
    (convert #(-> % :type/id) data)))

(x.core/reg-transfer! ::transfer-types!
  {:data-f      transfer-types-f
   :target-path [:site :types]})


;; -----------------------------------------------------------------------------
;; -----------------------------------------------------------------------------

(def products-projection
  {:projection #:product{:added-at    0
                         :added-by    0
                         :modified-at 0
                         :modified-by 0}})
            
(defn transfer-products-f
  [request]
  (let [data (mongo-db/get-collection "products" products-projection)]
       (convert #(-> % :product/id) data)))

(x.core/reg-transfer! ::transfer-products!
                      {:data-f      transfer-products-f
                       :target-path [:site :products]})

;; -----------------------------------------------------------------------------
;; -----------------------------------------------------------------------------

(def packages-projection
  {:projection #:package{:added-at    0
                         :added-by    0
                         :modified-at 0
                         :modified-by 0}})

(defn transfer-packages-f
  [request]
  (let [data (mongo-db/get-collection "packages" packages-projection)]
       (convert #(-> % :package/id) data)))

(x.core/reg-transfer! ::transfer-packgaes!
                      {:data-f      transfer-packages-f
                       :target-path [:site :packages]})

;; -----------------------------------------------------------------------------
;; -----------------------------------------------------------------------------

(def services-projection
  {:projection #:service{:added-at    0
                         :added-by    0
                         :modified-at 0
                         :modified-by 0}})

(defn transfer-services-f
  [request]
  (let [data (mongo-db/get-collection "services" services-projection)]
       (convert #(-> % :service/id) data)))

(x.core/reg-transfer! ::transfer-services!
                      {:data-f      transfer-services-f
                       :target-path [:site :services]})