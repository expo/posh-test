(ns posh-test.core
  (:require [reagent.core :as r]
            [posh.reagent :as p]
            [datascript.core :as d]
            [cljs.tools.reader :refer [read-string]]
            [cljs.js :refer [empty-state eval js-eval]]))


(defonce conn (d/create-conn))


(def ReactNative (js/require "react-native"))

(def text (r/adapt-react-class (.-Text ReactNative)))
(def view (r/adapt-react-class (.-View ReactNative)))

;; just lists all the keys and values
(defn map-editor [m]
  [view {:style {:border-width 5
                 :border-color "black"}}
   (for [[attr val] m]
     [view {:key attr
            :style {:margin 2
                    :flex-direction "row"}}
      [view {:style {:padding 2
                     :background-color "#eee"
                     :border-width 1
                     :border-color "#aaa"}}
       [text {:style {:font-weight "600"}}
        (str attr)]]
      [view {:style {:padding 2
                     :margin-left 2
                     :border-width 1
                     :border-color "#aaa"}}
       [text
        (str val)]]])])

;; renders entity's `pull` as a `map-editor``
(defn entity-view [entity-id]
  (let [entity @(p/pull conn '[*] entity-id)]
    [map-editor entity]))

(defn app-root []
  [view {:style {:flex 1
                 :align-items "center"
                 :justify-content "center"}}
   ;; below code finds ALL entities that have ANY attribute
   ;; renders with `entity-view` above
   (doall (for [entity-id @(p/q '[:find [?e ...]
                                  :in $
                                  :where [?e _ _]]
                                conn)]
            ^{:key entity-id}
            [view {:style {:margin 4}}
             [entity-view entity-id]]))])


(defn init []
  (p/posh! conn)

  (.registerComponent ReactNative.AppRegistry "main"
                      #(r/reactify-component app-root)))


(comment


  (p/transact! conn [{:todo/description "do something"}])

  (p/transact! conn [{:another/attribute "change this!"
                      :new/thing "blah"
                      :hello/world 3}])

  (p/transact! conn [{:another/attribute "kek"}])



  @(p/pull conn '[*] 2)


  ;; eval string within the app

  (defn test-eval [s]
    (eval (empty-state)
          (read-string s)
          {:eval js-eval
           :source-map true
           :context :expr}
          (fn [result] result)))

  (test-eval "(+ 1 2)")


  (cljs.pprint/pprint (vec (d/datoms @conn :eavt)))

  )

