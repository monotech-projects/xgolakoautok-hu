
(ns app.products.frontend.lister.lifecycles
    (:require [app.home.frontend.api]
              [x.core.api :as x.core]))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(x.core/reg-lifecycles! ::lifecycles
  {:on-app-boot {:dispatch-n [[:home.screen/add-menu-item! {:group-name :products-and-services
                                                            :icon       :fiber_manual_record
                                                            :icon-color "#48afad"
                                                            :label      :products
                                                            :on-click   [:x.router/go-to! "/@app-home/products"]
                                                            :horizontal-weight 0}]
                              [:home.sidebar/add-menu-item! {:group-name :products-and-services
                                                             :icon       :fiber_manual_record
                                                             :icon-color "#449997"
                                                             :label      :products
                                                             :on-click   [:x.router/go-to! "/@app-home/products"]
                                                             :vertical-weight 0}]]}})
