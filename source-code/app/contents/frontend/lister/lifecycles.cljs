
(ns app.contents.frontend.lister.lifecycles
    (:require [app.home.frontend.api]
              [x.core.api :as x.core]))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(x.core/reg-lifecycles! ::lifecycles
  {:on-app-boot {:dispatch-n [[:home.screen/add-menu-item! {:group-name  :content
                                                            :icon        :article
                                                            :icon-color  "#5564b1"
                                                            :icon-family :material-icons-outlined
                                                            :label       :contents
                                                            :on-click    [:x.router/go-to! "/@app-home/contents"]
                                                            :horizontal-weight 1}]
                              [:home.sidebar/add-menu-item! {:group-name  :content
                                                             :icon        :article
                                                             :icon-color  "#5c72e5"
                                                             :icon-family :material-icons-outlined
                                                             :label       :contents
                                                             :on-click    [:x.router/go-to! "/@app-home/contents"]
                                                             :vertical-weight 1}]]}})
