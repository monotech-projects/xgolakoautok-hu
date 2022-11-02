
(ns app.products.frontend.viewer.effects
    (:require [app.products.frontend.viewer.views :as viewer.views]
              [re-frame.api                       :as r]))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(r/reg-event-fx :products.viewer/load-viewer!
  {:dispatch-n [[:gestures/init-view-handler! :products.viewer
                                              {:default-view-id :overview}]
                [:ui/render-surface! :products.viewer/view
                                     {:content #'viewer.views/view}]]})
