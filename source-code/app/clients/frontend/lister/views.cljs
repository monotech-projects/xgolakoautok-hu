
(ns app.clients.frontend.lister.views
    (:require [app.common.frontend.api :as common]
              [elements.api            :as elements]
              [engines.item-lister.api :as item-lister]
              [layouts.surface-a.api   :as surface-a]
              [mid-fruits.keyword      :as keyword]
              [re-frame.api            :as r]))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn- footer
  []
  (if-let [first-data-received? @(r/subscribe [:item-lister/first-data-received? :clients.lister])]
          [common/item-lister-download-info :clients.lister {}]))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn- client-item-structure
  [lister-id item-dex {:keys [colors email-address id modified-at phone-number]}]
  (let [client-name @(r/subscribe [:clients.lister/get-client-name  item-dex])
        timestamp   @(r/subscribe [:activities/get-actual-timestamp modified-at])]
       [common/list-item-structure lister-id item-dex
                                   {:cells [[elements/color-marker   {:colors colors :indent {:left :xs :right :m :horizontal :xs} :size :m}]
                                            [common/list-item-label  lister-id item-dex {:content client-name :stretch? true :placeholder :unnamed-client}]
                                            [common/list-item-detail lister-id item-dex {:content email-address :width "240px"}]
                                            [common/list-item-detail lister-id item-dex {:content phone-number  :width "240px"}]
                                            [common/list-item-detail lister-id item-dex {:content timestamp     :width "160px"}]
                                            [common/list-item-marker lister-id item-dex {:icon    :navigate_next}]]}]))

(defn- client-item
  [lister-id item-dex {:keys [id] :as client-item}]
  [elements/toggle {:content     [client-item-structure lister-id item-dex client-item]
                    :hover-color :highlight
                    :on-click    [:router/go-to! (str "/@app-home/clients/"id)]}])

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn- client-list
  [lister-id items]
  [common/item-list lister-id {:item-element #'client-item :items items}])

(defn- client-lister-body
  []
  [item-lister/body :clients.lister
                    {:default-order-by :modified-at/descending
                     :items-path       [:clients :lister/downloaded-items]
                     :error-element    [common/error-content {:error :the-content-you-opened-may-be-broken}]
                     :ghost-element    #'common/item-lister-ghost-element
                     :list-element     #'client-list}])

(defn- client-lister-header
  []
  [common/item-lister-header :clients.lister
                             {:cells [[common/item-lister-header-spacer :clients.lister {:width "42px"}]
                                      [common/item-lister-header-cell   :clients.lister {:label :name          :order-by-key :name          :stretch? true}]
                                      [common/item-lister-header-cell   :clients.lister {:label :email-address :order-by-key :email-address :width "240px"}]
                                      [common/item-lister-header-cell   :clients.lister {:label :phone-number  :order-by-key :phone-number  :width "240px"}]
                                      [common/item-lister-header-cell   :clients.lister {:label :last-modified :order-by-key :modified-at   :width "160px"}]
                                      [common/item-lister-header-spacer :clients.lister {:width "36px"}]]}])

(defn- body
  []
  [common/item-lister-wrapper :clients.lister
                              {:body   #'client-lister-body
                               :header #'client-lister-header}])

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn create-item-button
  []
  (let [lister-disabled? @(r/subscribe [:item-lister/lister-disabled? :clients.lister])
        create-client-uri (str "/@app-home/clients/create")]
       [common/item-lister-create-item-button :clients.lister
                                              {:disabled?       lister-disabled?
                                               :create-item-uri create-client-uri}]))

(defn- search-field
  []
  (let [lister-disabled? @(r/subscribe [:item-lister/lister-disabled? :clients.lister])]
       [common/item-lister-search-field :clients.lister
                                        {:disabled?   lister-disabled?
                                         :placeholder :search-in-clients
                                         :search-keys [:name :email-address :phone-number]}]))

(defn- search-description
  []
  (let [lister-disabled? @(r/subscribe [:item-lister/lister-disabled? :clients.lister])]
       [common/item-lister-search-description :clients.lister
                                              {:disabled? lister-disabled?}]))

(defn- breadcrumbs
  []
  (let [lister-disabled? @(r/subscribe [:item-lister/lister-disabled? :clients.lister])]
       [common/surface-breadcrumbs :clients.lister/view
                                   {:crumbs [{:label :app-home :route "/@app-home"}
                                             {:label :clients}]
                                    :disabled? lister-disabled?}]))

(defn- label
  []
  (let [lister-disabled? @(r/subscribe [:item-lister/lister-disabled? :clients.lister])]
       [common/surface-label :clients.lister/view
                             {:disabled? lister-disabled?
                              :label     :clients}]))

(defn- header
  []
  (if-let [first-data-received? @(r/subscribe [:item-lister/first-data-received? :clients.lister])]
          [:<> [:div {:style {:display "flex" :justify-content "space-between" :flex-wrap "wrap" :grid-row-gap "24px"}}
                     [:div [label]
                           [breadcrumbs]]
                     [:div [create-item-button]]]
               [search-field]
               [search-description]]
          [common/item-lister-ghost-header :clients.lister {}]))

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