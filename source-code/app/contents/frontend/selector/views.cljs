
(ns app.contents.frontend.selector.views
    (:require [app.common.frontend.api               :as common]
              [app.contents.frontend.handler.helpers :as handler.helpers]
              [elements.api                          :as elements]
              [engines.item-lister.api               :as item-lister]
              [layouts.popup-a.api                   :as popup-a]
              [mid-fruits.hiccup                     :as hiccup]
              [re-frame.api                          :as r]))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn content-item-structure
  [selector-id item-dex {:keys [body name id modified-at] :as content-item}]
  (let [timestamp   @(r/subscribe [:activities/get-actual-timestamp modified-at])
        content-body (-> body handler.helpers/parse-content-body hiccup/to-string)]
       [common/list-item-structure selector-id item-dex
                                   {:cells [[common/list-item-thumbnail-icon selector-id item-dex {:icon :article :icon-family :material-icons-outlined}]
                                            ; HACK#4506
                                            [:div {:style {:flex-grow 1 :max-width "calc(100% - 144px)" :padding-right "24px"}}
                                                  [common/list-item-primary-cell selector-id item-dex {:label name
                                                                                                       :placeholder :unnamed-content
                                                                                                       :description content-body
                                                                                                       :timestamp timestamp}]]
                                            [common/selector-item-marker selector-id item-dex {:item-id id}]]}]))

(defn content-item
  [selector-id item-dex {:keys [id] :as content-item}]
  [elements/toggle {:content     [content-item-structure selector-id item-dex content-item]
                    :hover-color :highlight
                    :on-click    [:item-selector/item-clicked :contents.selector id]}])

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn- content-list
  [lister-id items]
  [common/item-list lister-id {:item-element #'content-item :items items}])

(defn- content-lister
  []
  [item-lister/body :contents.selector
                    {:default-order-by :modified-at/descending
                     :items-path       [:contents :selector/downloaded-items]
                     :error-element    [common/error-content {:error :the-content-you-opened-may-be-broken}]
                     :ghost-element    #'common/item-selector-ghost-element
                     :list-element     #'content-list}])

(defn- body
  []
  [:<> [elements/horizontal-separator {:size :xs}]
       [content-lister]])

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn- control-bar
  []
  (let [selector-disabled? @(r/subscribe [:item-lister/lister-disabled? :contents.selector])]
       [common/item-selector-control-bar :contents.selector
                                         {:disabled?        selector-disabled?
                                          :order-by-options [:modified-at/ascending :modified-at/descending :name/ascending :name/descending]
                                          :search-field-placeholder :search-in-contents
                                          :search-keys      [:name]}]))

(defn- label-bar
  []
  (let [multi-select? @(r/subscribe [:item-lister/get-meta-item :contents.selector :multi-select?])]
       [common/popup-label-bar :contents.selector/view
                               {:primary-button   {:label :save! :on-click [:item-selector/save-selection! :contents.selector]}
                                :secondary-button (if-let [autosaving? @(r/subscribe [:item-selector/autosaving? :contents.selector])]
                                                          {:label :abort!  :on-click [:item-selector/abort-autosave! :contents.selector]}
                                                          {:label :cancel! :on-click [:ui/remove-popup! :contents.selector/view]})
                                :label            (if multi-select? :select-contents! :select-content!)}]))

(defn- header
  []
  [:<> [label-bar]
       (if-let [first-data-received? @(r/subscribe [:item-lister/first-data-received? :contents.selector])]
               [control-bar]
               [elements/horizontal-separator {:size :xxl}])])

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn- footer
  []
  (let [selected-content-count @(r/subscribe [:item-lister/get-selected-item-count :contents.selector])
        on-discard-selection [:item-lister/discard-selection! :contents.selector]]
       [common/item-selector-footer :contents.selector
                                    {:on-discard-selection on-discard-selection
                                     :selected-item-count  selected-content-count}]))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn view
  [selector-id]
  [popup-a/layout :contents.selector/view
                  {:body                #'body
                   :header              #'header
                   :footer              #'footer
                   :min-width           :m
                   :stretch-orientation :vertical}])