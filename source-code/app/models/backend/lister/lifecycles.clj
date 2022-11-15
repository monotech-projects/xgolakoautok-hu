
(ns app.models.backend.lister.lifecycles
    (:require [engines.item-lister.api]
              [x.core.api :as x.core]))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(x.core/reg-lifecycles! ::lifecycles
  {:on-server-boot [:item-lister/init-lister! :models.lister
                                              {:base-route      "/@app-home/models"
                                               :collection-name "models"
                                               :handler-key     :models.lister
                                               :item-namespace  :model
                                               :on-route        [:models.lister/load-lister!]
                                               :route-title     :models}]})
