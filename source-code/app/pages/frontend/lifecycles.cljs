
(ns app.pages.frontend.lifecycles
    (:require [app.pages.frontend.dictionary :as dictionary]
              [x.app-core.api                :as x.core]))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(x.core/reg-lifecycles! ::lifecycles
  {:on-app-boot [:dictionary/add-terms! dictionary/BOOK]})