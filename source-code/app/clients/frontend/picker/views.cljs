
(ns app.clients.frontend.picker.views
    (:require [app.clients.frontend.picker.prototypes :as picker.prototypes]
              [app.clients.frontend.preview.views     :as preview.views]
              [elements.api                           :as elements]
              [mid-fruits.random                      :as random]))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn- client-picker-previews
  ; @param (keyword) picker-id
  ; @param (map) picker-props
  [picker-id picker-props]
  (let [preview-props (picker.prototypes/preview-props-prototype picker-id picker-props)]
       [preview.views/element ::client-picker-previews preview-props]))

(defn- client-picker-button
  ; @param (keyword) picker-id
  ; @param (map) picker-props
  ;  {:disabled? (boolean)(opt)
  ;   :multi-select? (boolean)(opt)}
  [picker-id {:keys [disabled? multi-select?] :as picker-props}]
  (let [on-click [:clients.selector/load-selector! :clients.selector picker-props]]
       [:div {:style {:display :flex}}
             [elements/button {:color     :muted
                               :disabled? disabled?
                               :font-size :xs
                               :label     (if multi-select? :select-clients! :select-client!)
                               :on-click  on-click}]]))

(defn- client-picker-label
  ; @param (keyword) picker-id
  ; @param (map) picker-props
  ;  {:disabled? (boolean)(opt)
  ;   :info-text (metamorphic-content)(opt)
  ;   :label (metamorphic-content)(opt)
  ;   :required? (boolean)(opt)}
  [_ {:keys [disabled? info-text label required?]}]
  (if label [elements/label {:content   label
                             :disabled? disabled?
                             :info-text info-text
                             :required? required?}]))

(defn- client-picker-body
  ; @param (keyword) picker-id
  ; @param (map) picker-props
  [picker-id picker-props]
  [:<> [client-picker-label    picker-id picker-props]
       [client-picker-button   picker-id picker-props]
       [client-picker-previews picker-id picker-props]])

(defn- client-picker
  ; @param (keyword) picker-id
  ; @param (map) picker-props
  ;  {}
  [picker-id {:keys [indent] :as picker-props}]
  [elements/blank picker-id
                  {:content [client-picker-body picker-id picker-props]
                   :indent  indent}])

(defn element
  ; @param (keyword)(opt) picker-id
  ; @param (map) picker-props
  ;  {:autosave? (boolean)(opt)
  ;    Default: false
  ;   :disabled? (boolean)(opt)
  ;    Default: false
  ;   :indent (map)(opt)
  ;   :info-text (metamorphic-content)(opt)
  ;   :label (metamorphic-content)(opt)
  ;   :max-count (integer)(opt)
  ;    Default: 8
  ;   :multi-select? (boolean)(opt)
  ;    Default: false
  ;   :on-change (metamorphic-event)(opt)
  ;    Az esemény utolsó paraméterként megkapja a kiválasztott elemet.
  ;   :on-save (metamorphic-event)(opt)
  ;    Az esemény utolsó paraméterként megkapja a kiválasztott elemet.
  ;   :placeholder (metamorphic-content)(opt)
  ;   :required? (boolean)(opt)
  ;    Default: false
  ;   :value-path (vector)}
  ;
  ; @usage
  ;  [client-picker {...}]
  ;
  ; @usage
  ;  [client-picker :my-picker {...}]
  ([picker-props]
   [element (random/generate-keyword) picker-props])

  ([picker-id picker-props]
   (let [picker-props (picker.prototypes/picker-props-prototype picker-id picker-props)]
        [client-picker picker-id picker-props])))
