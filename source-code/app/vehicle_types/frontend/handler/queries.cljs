
(ns app.vehicle-types.frontend.handler.queries
    (:require [re-frame.api :as r]))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn request-model-name-query
  ; @return (vector)
  []
  ; XXX#4800
  (let [model-id @(r/subscribe [:x.router/get-current-route-path-param :model-id])]
       [`(~:vehicle-types.handler/get-model-name ~{:model-id model-id})]))
