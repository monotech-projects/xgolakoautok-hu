
(ns site.xgo.pages.main-page.frontend.sections.categories.subs
  (:require [re-frame.api :as r]
            [mid-fruits.normalize :as normalize]))

; TEMP
(defn normalize-str [text]
  (-> text (str)
           (normalize/deaccent)
           (normalize/cut-special-chars "-+")
           (normalize/space->separator)
           (clojure.string/lower-case)))

;; -----------------------------------------------------------------------------
;; -----------------------------------------------------------------------------

(r/reg-sub
  :category/description
  (fn [db [_]]
    (let [category-name (get-in db [:filters :category] "dynamic")]
        (get-in db [:site :categories category-name]))))

(r/reg-sub
  :categories/selected?
  (fn [db [_ category-name]]
    (-> db
        (get-in [:filters :category] "dynamic")
        (= (normalize-str category-name)))))
