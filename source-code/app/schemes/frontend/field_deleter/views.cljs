
(ns app.schemes.frontend.field-deleter.views
    (:require [app.common.frontend.api     :as common]
              [app.components.frontend.api :as components]
              [elements.api                :as elements]
              [layouts.popup-a.api         :as popup-a]
              [re-frame.api                :as r]))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn label-bar
  ; @param (keyword) scheme-id
  ; @param (keyword) field-id
  [scheme-id field-id]
  [components/popup-label-bar ::label-bar
                              {:primary-button   {:color :warning
                                                  :on-click [:schemes.field-deleter/delete-field! scheme-id field-id]
                                                  :label    :delete!
                                                  :keypress {:key-code 13}}
                               :secondary-button {:on-click [:x.ui/remove-popup! :schemes.field-deleter/consent]
                                                  :label    :cancel!
                                                  :keypress {:key-code 27}}}])

(defn field-name-label
  ; @param (keyword) scheme-id
  ; @param (keyword) field-id
  [scheme-id field-id]
  (let [{:field/keys [name]} @(r/subscribe [:schemes.form-handler/get-scheme-field scheme-id field-id])]
       [elements/label ::field-name-label
                       {:color            :muted
                        :content          name
                        :font-size        :m
                        :indent           {:bottom :xs}
                        :horizontal-align :center
                        :line-height      :block}]))

(defn data-lost-label
  ; @param (keyword) scheme-id
  ; @param (keyword) field-id
  [scheme-id field-id]
  [elements/label ::data-lost-label
                  {:color            :warning
                   :content          :all-data-stored-in-this-field-will-be-lost
                   :font-size        :s
                   :font-weight      :extra-bold
                   :horizontal-align :center
                   :indent           {:bottom :xs :vertical :s}
                   :line-height      :block}])

(defn delete-field-label
  ; @param (keyword) scheme-id
  ; @param (keyword) field-id
  [scheme-id field-id]
  [elements/label ::delete-field-label
                  {:content          :delete-field?
                   :horizontal-align :center
                   :indent           {:vertical :s}
                   :line-height      :block}])

(defn consent
  ; @param (keyword) scheme-id
  ; @param (keyword) field-id
  [scheme-id field-id]
  [:<> [label-bar          scheme-id field-id]
       [field-name-label   scheme-id field-id]
       [delete-field-label scheme-id field-id]
       [data-lost-label    scheme-id field-id]])

(defn- delete-failed-label
  ; @param (keyword) scheme-id
  ; @param (keyword) field-id
  [_ _]
  [components/popup-progress-indicator ::delete-failed-label
                                       {:color  :warning
                                        :label  :failed-to-delete
                                        :indent {:horizontal :xxl}}])

(defn- deleting-label
  ; @param (keyword) scheme-id
  ; @param (keyword) field-id
  [_ _]
  [components/popup-progress-indicator ::deleting-label
                                       {:label  :deleting-field...
                                        :indent {:horizontal :xxl}}])

(defn- body
  ; @param (keyword) scheme-id
  ; @param (keyword) field-id
  [scheme-id field-id]
  (let [deleter-status @(r/subscribe [:x.db/get-item [:schemes :field-deleter/meta-items :deleter-status]])]
       (case deleter-status :deleting      [deleting-label      scheme-id field-id]
                            :delete-failed [delete-failed-label scheme-id field-id]
                                           [consent             scheme-id field-id])))

(defn view
  ; @param (keyword) scheme-id
  ; @param (keyword) field-id
  [scheme-id field-id]
  [popup-a/layout :schemes.field-deleter/consent
                  {:body [body scheme-id field-id]
                   :min-width :xxs}])
