
(ns site.xgo.pages.main-page.frontend.sections.categories.effects
  (:require [re-frame.api         :as r]
            [mid-fruits.normalize :as normalize]))

;; -----------------------------------------------------------------------------
;; -----------------------------------------------------------------------------

(r/reg-fx
  ::set-url!
  (fn [url]
    (r/dispatch [:router/swap-to! url])))

(r/reg-event-fx
  :categories/select!
  (fn [_ [_ name]]
    {:dispatch-n [[:db/set-item! [:filters :category] (normalize/clean-text name "-+")]]
     :url/set-url! (str "/" (normalize/clean-text name "-+"))}))