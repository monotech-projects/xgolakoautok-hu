
(ns app.packages.frontend.lister.views
    (:require [app.common.frontend.api :as common]
              [elements.api            :as elements]
              [engines.item-lister.api :as item-lister]
              [layouts.surface-a.api   :as surface-a]
              [re-frame.api            :as r]))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn- footer
  []
  (if-let [first-data-received? @(r/subscribe [:item-lister/first-data-received? :packages.lister])]
          [common/item-lister-download-info :packages.lister {}]))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn- package-item-structure
  [lister-id item-dex {:keys [item-number modified-at name products thumbnail]}]
  (let [timestamp    @(r/subscribe [:activities/get-actual-timestamp modified-at])
        product-count (count products)
        description   {:content :n-items :replacements [product-count]}]
       [common/list-item-structure lister-id item-dex
                                   {:cells [[common/list-item-thumbnail    lister-id item-dex {:thumbnail (:media/uri thumbnail)}]
                                            [common/list-item-primary-cell lister-id item-dex {:label name :stretch? true :placeholder :unnamed-package :description description}]
                                            [common/list-item-detail       lister-id item-dex {:content item-number :width "160px"}]
                                            [common/list-item-detail       lister-id item-dex {:content timestamp :width "160px"}]
                                            [common/list-item-marker       lister-id item-dex {:icon :navigate_next}]]}]))

(defn- package-item
  [lister-id item-dex {:keys [id] :as package-item}]
  [elements/toggle {:content     [package-item-structure lister-id item-dex package-item]
                    :hover-color :highlight
                    :on-click    [:router/go-to! (str "/@app-home/packages/"id)]}])

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn- package-list
  [lister-id items]
  [common/item-list lister-id {:item-element #'package-item :items items}])

(defn- package-lister-body
  []
  [item-lister/body :packages.lister
                    {:default-order-by :modified-at/descending
                     :items-path       [:packages :lister/downloaded-items]
                     :error-element    [common/error-content {:error :the-content-you-opened-may-be-broken}]
                     :ghost-element    #'common/item-lister-ghost-element
                     :list-element     #'package-list}])

(defn- package-lister-header
  []
  [common/item-lister-header :packages.lister
                             {:cells [[common/item-lister-header-spacer :packages.lister {:width "108px"}]
                                      [common/item-lister-header-cell   :packages.lister {:label :name          :order-by-key :name :stretch? true}]
                                      [common/item-lister-header-cell   :products.lister {:label :item-number   :order-by-key :item-number :width "160px"}]
                                      [common/item-lister-header-cell   :packages.lister {:label :last-modified :order-by-key :modified-at :width "160px"}]
                                      [common/item-lister-header-spacer :packages.lister {:width "36px"}]]}])

(defn- body
  []
  [common/item-lister-wrapper :packages.lister
                              {:body   #'package-lister-body
                               :header #'package-lister-header}])

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn create-item-button
  []
  (let [lister-disabled? @(r/subscribe [:item-lister/lister-disabled? :packages.lister])
        create-package-uri (str "/@app-home/packages/create")]
       [common/item-lister-create-item-button :packages.lister
                                              {:disabled?       lister-disabled?
                                               :create-item-uri create-package-uri}]))

(defn- search-field
  []
  (let [lister-disabled? @(r/subscribe [:item-lister/lister-disabled? :packages.lister])]
       [common/item-lister-search-field :packages.lister
                                        {:disabled?   lister-disabled?
                                         :placeholder :search-in-packages
                                         :search-keys [:item-number :name]}]))

(defn- search-description
  []
  (let [lister-disabled? @(r/subscribe [:item-lister/lister-disabled? :packages.lister])]
       [common/item-lister-search-description :packages.lister
                                              {:disabled? lister-disabled?}]))

(defn- breadcrumbs
  []
  (let [lister-disabled? @(r/subscribe [:item-lister/lister-disabled? :packages.lister])]
       [common/surface-breadcrumbs :packages.lister/view
                                   {:crumbs [{:label :app-home :route "/@app-home"}
                                             {:label :packages}]
                                    :disabled? lister-disabled?}]))

(defn- label
  []
  (let [lister-disabled? @(r/subscribe [:item-lister/lister-disabled? :packages.lister])]
       [common/surface-label :packages.lister/view
                             {:disabled? lister-disabled?
                              :label     :packages}]))

(defn- header
  []
  (if-let [first-data-received? @(r/subscribe [:item-lister/first-data-received? :packages.lister])]
          [:<> [:div {:style {:display "flex" :justify-content "space-between" :flex-wrap "wrap" :grid-row-gap "24px"}}
                     [:div [label]
                           [breadcrumbs]]
                     [:div [create-item-button]]]
               [search-field]
               [search-description]]
          [common/item-lister-ghost-header :packages.lister {}]))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn- view-structure
  []
  [:<> [header]
       [body]
       [footer]])

(defn view
  [surface-id]
  [surface-a/layout surface-id
                    {:content #'view-structure}])