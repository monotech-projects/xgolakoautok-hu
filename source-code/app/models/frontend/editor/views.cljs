
(ns app.models.frontend.editor.views
    (:require [app.common.frontend.api  :as common]
              [app.storage.frontend.api :as storage]
              [elements.api             :as elements]
              [engines.item-editor.api  :as item-editor]
              [engines.item-lister.api  :as item-lister]
              [forms.api                :as forms]
              [layouts.surface-a.api    :as surface-a]
              [re-frame.api             :as r]))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

; meta-keywords    = tags
; meta-description = description

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn- model-thumbnail-picker
  []
  (let [editor-disabled? @(r/subscribe [:item-editor/editor-disabled? :models.editor])]
       [storage/media-picker ::model-thumbnail-picker
                             {:autosave?     true
                              :disabled?     editor-disabled?
                              :extensions    ["bmp" "jpg" "jpeg" "png" "webp"]
                              :indent        {:vertical :s}
                              :multi-select? false
                              :placeholder   "-"
                              :toggle-label  :select-image!
                              :thumbnail     {:height :3xl :width :5xl}
                              :value-path    [:models :editor/edited-item :thumbnail]}]))

(defn- model-thumbnail-box
  []
  (let [editor-disabled? @(r/subscribe [:item-editor/editor-disabled? :models.editor])]
       [common/surface-box ::model-thumbnail-box
                           {:content [:<> [:div (forms/form-row-attributes)
                                                [:div (forms/form-block-attributes {:ratio 100})
                                                      [model-thumbnail-picker]]]
                                          [elements/horizontal-separator {:size :s}]]
                            :disabled? editor-disabled?
                            :label     :thumbnail}]))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn- model-thumbnail
  []
  [:<> [model-thumbnail-box]])

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn- model-description-field
  []
  (let [editor-disabled? @(r/subscribe [:item-editor/editor-disabled? :models.editor])]
       [elements/multiline-field ::model-description-field
                                 {:disabled?   editor-disabled?
                                  :indent      {:top :m :vertical :s}
                                  :label       :description
                                  :placeholder :model-description-placeholder
                                  :value-path  [:models :editor/edited-item :description]}]))

(defn- model-tags-field
  []
  (let [editor-disabled? @(r/subscribe [:item-editor/editor-disabled? :models.editor])]
       [elements/multi-combo-box ::model-tags-field
                                 {:disabled?    editor-disabled?
                                  :indent       {:top :m :vertical :s}
                                  :label        :tags
                                  :options-path [:models :editor/suggestions :tags]
                                  :placeholder  :model-tags-placeholder
                                  :value-path   [:models :editor/edited-item :tags]}]))

(defn- model-name-field
  []
  (let [editor-disabled? @(r/subscribe [:item-editor/editor-disabled? :models.editor])]
       [elements/combo-box ::model-name-field
                           {:autofocus?   true
                            :disabled?    editor-disabled?
                            :emptiable?   false
                            :indent       {:top :m :vertical :s}
                            :label        :name
                            :options-path [:models :editor/suggestions :name]
                            :placeholder  :model-name-placeholder
                            :required?    true
                            :value-path   [:models :editor/edited-item :name]}]))

(defn- model-product-description-field
  []
  (let [editor-disabled? @(r/subscribe [:item-editor/editor-disabled? :models.editor])]
       [elements/combo-box ::model-product-description-field
                           {:disabled?    editor-disabled?
                            :emptiable?   false
                            :indent       {:top :m :vertical :s}
                            :label        :product-description
                            :options-path [:models :editor/suggestions :product-description]
                            :placeholder  :model-product-description-placeholder
                            :required?    true
                            :value-path   [:models :editor/edited-item :product-description]}]))

(defn- model-data-box
  []
  (let [editor-disabled? @(r/subscribe [:item-editor/editor-disabled? :models.editor])]
       [common/surface-box ::model-data-box
                           {:content [:<> [:div (forms/form-row-attributes)
                                                [:div (forms/form-block-attributes {:ratio 100})
                                                      [model-name-field]]
                                                [:div (forms/form-block-attributes {:ratio 100})
                                                      [model-product-description-field]]]
                                          [:div (forms/form-row-attributes)
                                                [:div (forms/form-block-attributes {:ratio 100})
                                                      [model-tags-field]]]
                                          [:div (forms/form-row-attributes)
                                                [:div (forms/form-block-attributes {:ratio 100})
                                                      [model-description-field]]]
                                          [elements/horizontal-separator {:size :s}]]
                            :disabled? editor-disabled?
                            :label     :data}]))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn- model-data
  []
  [:<> [model-data-box]])

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn- body
  []
  (let [current-view-id @(r/subscribe [:gestures/get-current-view-id :models.editor])]
       (case current-view-id :data      [model-data]
                             :thumbnail [model-thumbnail])))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn- menu-bar
  []
  (let [editor-disabled? @(r/subscribe [:item-editor/editor-disabled? :models.editor])]
       [common/item-editor-menu-bar :models.editor
                                    {:disabled?  editor-disabled?
                                     :menu-items [{:label :data      :change-keys [:name :product-description :tags :description]}
                                                  {:label :thumbnail :change-keys [:thumbnail]}]}]))

(defn- controls
  []
  (let [editor-disabled? @(r/subscribe [:item-editor/editor-disabled? :models.editor])]
       [common/item-editor-controls :models.editor
                                    {:disabled? editor-disabled?}]))

(defn- breadcrumbs
  []
  (let [editor-disabled? @(r/subscribe [:item-editor/editor-disabled? :models.editor])
        model-name       @(r/subscribe [:db/get-item [:models :editor/edited-item :name]])
        model-id         @(r/subscribe [:router/get-current-route-path-param :item-id])
        model-uri         (str "/@app-home/models/" model-id)]
       [common/surface-breadcrumbs :models.editor/view
                                   {:crumbs (if model-id [{:label :app-home  :route "/@app-home"}
                                                          {:label :models    :route "/@app-home/models"}
                                                          {:label model-name :route model-uri :placeholder :unnamed-model}
                                                          {:label :edit!}]
                                                         [{:label :app-home  :route "/@app-home"}
                                                          {:label :models    :route "/@app-home/models"}
                                                          {:label :add!}])
                                    :disabled? editor-disabled?}]))

(defn- label
  []
  (let [editor-disabled? @(r/subscribe [:item-editor/editor-disabled? :models.editor])
        model-name       @(r/subscribe [:db/get-item [:models :editor/edited-item :name]])]
       [common/surface-label :models.editor/view
                             {:disabled?   editor-disabled?
                              :label       model-name
                              :placeholder :unnamed-model}]))

(defn- header
  []
  [:<> [:div {:style {:display "flex" :justify-content "space-between" :flex-wrap "wrap" :grid-row-gap "48px"}}
             [:div [label]
                   [breadcrumbs]]
             [:div [controls]]]
       [elements/horizontal-separator {:size :xxl}]
       [menu-bar]])

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn- view-structure
  []
  [:<> [header]
       [body]])

(defn- model-editor
  []
  [item-editor/body :models.editor
                    {:auto-title?      true
                     :form-element     #'view-structure
                     :error-element    [common/error-content {:error :the-item-you-opened-may-be-broken}]
                     :ghost-element    #'common/item-editor-ghost-element
                     :item-path        [:models :editor/edited-item]
                     :label-key        :name
                     :suggestion-keys  [:name :product-description :tags]
                     :suggestions-path [:models :editor/suggestions]}])

(defn view
  [surface-id]
  [surface-a/layout surface-id
                    {:content #'model-editor}])