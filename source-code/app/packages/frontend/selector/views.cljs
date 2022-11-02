
(ns app.packages.frontend.selector.views
    (:require [app.common.frontend.api :as common]
              [elements.api            :as elements]
              [engines.item-lister.api :as item-lister]
              [layouts.popup-a.api     :as popup-a]
              [re-frame.api            :as r]))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn- footer
  []
  (let [selected-package-count @(r/subscribe [:item-lister/get-selected-item-count :packages.selector])
        on-discard-selection [:item-lister/discard-selection! :packages.selector]]
       [common/item-selector-footer :packages.selector
                                    {:on-discard-selection on-discard-selection
                                     :selected-item-count  selected-package-count}]))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn- package-item-structure
  [selector-id item-dex {:keys [id modified-at name thumbnail quantity-unit]}]
  (let [timestamp     @(r/subscribe [:activities/get-actual-timestamp modified-at])
        package-count @(r/subscribe [:item-selector/get-item-count selector-id id])]
       [common/list-item-structure selector-id item-dex
                                   {:cells [[common/selector-item-counter  selector-id item-dex {:item-id id}]
                                            [common/list-item-thumbnail    selector-id item-dex {:thumbnail (:media/uri thumbnail)}]
                                            [common/list-item-primary-cell selector-id item-dex {:label name :timestamp timestamp :stretch? true :placeholder :unnamed-package
                                                                                                 :description {:content (:value quantity-unit) :replacements [package-count]}}]
                                            [common/selector-item-marker   selector-id item-dex {:item-id id}]]}]))

(defn- package-item
  [selector-id item-dex {:keys [id] :as package-item}]
  [elements/toggle {:content     [package-item-structure selector-id item-dex package-item]
                    :hover-color :highlight
                    :on-click    [:item-selector/item-clicked :packages.selector id]}])

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn- package-list
  [lister-id items]
  [common/item-list lister-id {:item-element #'package-item :items items}])

(defn- package-lister
  []
  [item-lister/body :packages.selector
                    {:default-order-by :modified-at/descending
                     :items-path       [:packages :selector/downloaded-items]
                     :error-element    [common/error-content {:error :the-content-you-opened-may-be-broken}]
                     :ghost-element    #'common/item-selector-ghost-element
                     :list-element     #'package-list}])

(defn- body
  []
  [:<> [elements/horizontal-separator {:size :xs}]
       [package-lister]])

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn- control-bar
  []
  (let [selector-disabled? @(r/subscribe [:item-lister/lister-disabled? :packages.selector])]
       [common/item-selector-control-bar :packages.selector
                                         {:disabled?        selector-disabled?
                                          :order-by-options [:modified-at/ascending :modified-at/descending :name/ascending :name/descending]
                                          :search-field-placeholder :search-in-packages
                                          :search-keys      [:item-number :name]}]))

(defn- label-bar
  []
  (let [multi-select? @(r/subscribe [:item-lister/get-meta-item :packages.selector :multi-select?])]
       [common/popup-label-bar :packages.selector/view
                               {:primary-button   {:label :save!   :on-click [:item-selector/save-selection! :packages.selector]}
                                :secondary-button {:label :cancel! :on-click [:ui/remove-popup! :packages.selector/view]}
                                :label            (if multi-select? :select-packages! :select-package!)}]))

(defn- header
  []
  [:<> [label-bar]
       (if-let [first-data-received? @(r/subscribe [:item-lister/first-data-received? :packages.selector])]
               [control-bar]
               [elements/horizontal-separator {:size :xxl}])])

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn view
  [popup-id]
  [popup-a/layout popup-id
                  {:footer              #'footer
                   :body                #'body
                   :header              #'header
                   :min-width           :m
                   :stretch-orientation :vertical}])
