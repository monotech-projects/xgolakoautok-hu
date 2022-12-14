
(ns app.clients.frontend.viewer.effects
    (:require [app.clients.frontend.viewer.views :as viewer.views]
              [re-frame.api                      :as r]))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(r/reg-event-fx :clients.viewer/load-viewer!
  {:dispatch-n [[:x.gestures/init-view-handler! :clients.viewer
                                                {:default-view-id :overview}]
                [:clients.viewer/render-viewer!]]})

(r/reg-event-fx :clients.viewer/render-viewer!
  [:x.ui/render-surface! :clients.viewer/view
                         {:content #'viewer.views/view}])
