
(ns app.home.frontend.screen.subs
    (:require [candy.api :refer [return]]
              [re-frame.api     :as r]))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn get-menu-group-items
  ; @param (keyword) group-name
  ;
  ; @return (maps in vector)
  [db [_ group-name]]
  ; XXX#0092
  (let [menu-items (get-in db [:home :screen/menu-items] [])]
       (letfn [(f [group-items {:keys [group] :as menu-item}]
                  (if (=      group-name  group)
                      (conj   group-items menu-item)
                      (return group-items)))]
              (reduce f [] menu-items))))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(r/reg-sub :home.screen/get-menu-group-items get-menu-group-items)
