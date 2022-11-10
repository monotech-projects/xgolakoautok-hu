
(ns app.services.frontend.viewer.effects
    (:require [app.services.frontend.viewer.views :as viewer.views]
              [re-frame.api                       :as r]))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(r/reg-event-fx :services.viewer/load-viewer!
  {:dispatch-n [[:gestures/init-view-handler! :services.viewer
                                              {:default-view-id :overview}]
                [:ui/render-surface! :services.viewer/view
                                     {:content #'viewer.views/view}]]})