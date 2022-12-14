
(ns app.common.frontend.item-selector.events
    (:require [app.common.frontend.item-selector.subs :as item-selector.subs]
              [candy.api                              :refer [return]]
              [engines.item-lister.api                :as item-lister]
              [map.api                                :refer [dissoc-in]]
              [re-frame.api                           :as r :refer [r]]
              [vector.api                             :as vector]))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn import-selection!
  ; @param (keyword) selector-id
  ; @param (map) selector-props
  ;
  ; @return (map)
  [db [_ selector-id]]
  ; TODO#5060
  (let [stored-selection     (r item-selector.subs/get-stored-selection db selector-id)
        imported-selection   (r item-selector.subs/import-selection     db selector-id)
        imported-item-counts (r item-selector.subs/import-item-counts   db selector-id)]
       (as-> db % (r item-lister/import-selection! % selector-id imported-selection)
                  (assoc-in % [:engines :engine-handler/meta-items selector-id :item-counts]        imported-item-counts)
                  (assoc-in % [:engines :engine-handler/meta-items selector-id :exported-selection] stored-selection))))

(defn store-exported-selection!
  ; @param (keyword) selector-id
  ;
  ; @return (map)
  [db [_ selector-id]]
  (let [value-path         (r item-lister/get-meta-item           db selector-id :value-path)
        exported-selection (r item-selector.subs/export-selection db selector-id)]
       (assoc-in db value-path exported-selection)))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn toggle-exported-selection!
  ; @param (keyword) selector-id
  ; @param (string) item-id
  ;
  ; @return (map)
  [db [_ selector-id item-id]]
  ; XXX#6781
  ; Mivel az export-item-f f??ggv??ny m??sodik param??terk??nt megkapja az elemet,
  ; ez??rt sz??ks??ges a kiv??laszt??skor alkalmazni az export-item-f f??ggv??nyt,
  ; hogy az elem mindenk??pp let??lt??tt ??llapotban legyen (a f??ggv??ny alkalmaz??sakor)!
  ; Pl.: A storage.media-selector plugin a mapp??k b??ng??sz??sekor elt??vol??tja
  ;      az el??z??leg b??ng??szett mappa elemeit a Re-Frame adatb??zisb??l.
  ; Pl.: A keres??mez?? haszn??lata elt??vol??tja a keresnek nem megfelel?? elemeket az adatb??zisb??l.
  ;
  ; BUG#8001 (app.common.frontend.item-selector.subs)
  (let [export-item-f (r item-lister/get-meta-item db selector-id :export-item-f)
        item          (r item-lister/get-item      db selector-id item-id)
        item-count    (get-in db [:engines :engine-handler/meta-items selector-id :item-counts item-id] 1)
        exported-item (export-item-f item-id item item-count)]
       (update-in db [:engines :engine-handler/meta-items selector-id :exported-selection] vector/toggle-item exported-item)))

(defn toggle-exported-single-selection!
  ; @param (keyword) selector-id
  ; @param (string) item-id
  ;
  ; @return (map)
  [db [_ selector-id item-id]]
  ; XXX#6781
  ;
  ; BUG#8001 (app.common.frontend.item-selector.subs)
  (let [export-item-f (r item-lister/get-meta-item db selector-id :export-item-f)
        item          (r item-lister/get-item      db selector-id item-id)
        item-count    (get-in db [:engines :engine-handler/meta-items selector-id :item-counts item-id] 1)
        exported-item (export-item-f item-id item item-count)]
       (let [exported-selection (get-in db [:engines :engine-handler/meta-items selector-id :exported-selection])]
            (if (= exported-selection [exported-item])
                (dissoc-in db [:engines :engine-handler/meta-items selector-id :exported-selection])
                (assoc-in  db [:engines :engine-handler/meta-items selector-id :exported-selection] [exported-item])))))

(defn toggle-item-selection!
  ; @param (keyword) selector-id
  ; @param (string) item-id
  ;
  ; @return (map)
  [db [_ selector-id item-id]]
  (if-let [multi-select? (r item-lister/get-meta-item db selector-id :multi-select?)]
          (as-> db % (r item-lister/toggle-item-selection!        % selector-id item-id)
                     (r toggle-exported-selection!                % selector-id item-id))
          (as-> db % (r item-lister/toggle-single-item-selection! % selector-id item-id)
                     (r toggle-exported-single-selection!         % selector-id item-id))))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn increase-item-count!
  ; @param (keyword) selector-id
  ; @param (string) item-id
  ;
  ; @return (map)
  [db [_ selector-id item-id]]
  ; TODO#5060 lecser??lni majd a core.subs/set-meta-item! f??ggv??nyre!
  ;
  ; BUG#8001 (app.common.frontend.item-selector.subs)
  ;
  ; BUG#6781
  ; Mivel a kiv??laszt??sok export??l??sa a kiv??laszt??skor t??rt??nik meg ??s a kiv??laszt??sok
  ; ment??sekor m??r csak elt??rol??sra ker??l, ez??rt az elemsz??mok m??dos??t??sakor sz??ks??ges
  ; aktualiz??lni az adott elem export??lt kiv??laszt??s??t is.
  ; Mivel az export??lt elem nem felt??tlen??l egyezik meg a megv??ltozott elemsz??m?? v??ltozat??val,
  ; ez??rt az elemsz??m v??ltoz??sa el??tt kit??rli az elemet az export??lt kiv??laszt??sok k??z??l,
  ; majd a v??ltoz??s ut??n ??jraexport??lja azt.
  (as-> db % (r toggle-item-selection! % selector-id item-id)
             (if-let [item-count (get-in % [:engines :engine-handler/meta-items selector-id :item-counts item-id])]
                     (if (< item-count 256)
                         (update-in % [:engines :engine-handler/meta-items selector-id :item-counts item-id] inc)
                         (return    %))
                     (assoc-in % [:engines :engine-handler/meta-items selector-id :item-counts item-id] 2))
             (r toggle-item-selection! % selector-id item-id)))

(defn decrease-item-count!
  ; @param (keyword) selector-id
  ; @param (string) item-id
  ;
  ; @return (map)
  [db [_ selector-id item-id]]
  ; TODO#5060
  ;
  ; BUG#6781
  (as-> db % (r toggle-item-selection! % selector-id item-id)
             (let [item-count (get-in % [:engines :engine-handler/meta-items selector-id :item-counts item-id])]
                  (if (> item-count 1)
                      (update-in % [:engines :engine-handler/meta-items selector-id :item-counts item-id] dec)
                      (return    %)))
             (r toggle-item-selection! % selector-id item-id)))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn store-selector-props!
  ; @param (keyword) selector-id
  ; @param (map) selector-props
  ;  {:autosave? (boolean)(opt)
  ;   :multi-select? (boolean)(opt)
  ;   :on-save (metamorphic-event)(opt)
  ;   :value-path (vector)}
  ;
  ; @usage
  ;  (r store-selector-props! db :my-selector {...})
  ;
  ; @return (map)
  [db [_ selector-id selector-props]]
  (letfn [(f [db k v] (r item-lister/set-meta-item! db selector-id k v))]
         (reduce-kv f db selector-props)))

(defn load-selector!
  ; @param (keyword) selector-id
  ; @param (map) selector-props
  ;
  ; @return (map)
  [db [_ selector-id selector-props]]
  (as-> db % (r store-selector-props! % selector-id selector-props)
             (r import-selection!     % selector-id)))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn abort-autosave!
  ; @param (keyword) selector-id
  ;
  ; @return (map)
  [db [_ selector-id]]
  (r item-lister/set-meta-item! db selector-id :autosave-id))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

; @usage
;  [:item-selector/increase-item-count! :my-selector "my-item"]
(r/reg-event-db :item-selector/increase-item-count! increase-item-count!)

; @usage
;  [:item-selector/decrease-item-count! :my-selector "my-item"]
(r/reg-event-db :item-selector/decrease-item-count! decrease-item-count!)
