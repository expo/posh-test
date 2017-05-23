(ns posh-test.core
  (:require [reagent.core :as r]
            [posh.reagent :as p]

            [datascript.core :as d]))


(def conn (d/create-conn))


(def ReactNative (js/require "react-native"))

(def text (r/adapt-react-class (.-Text ReactNative)))
(def view (r/adapt-react-class (.-View ReactNative)))

(defn item-view [item-id]
  (let [item @(p/pull conn '[:todo/description] item-id)]
    [text {:key item-id} (:todo/description item)]))

(defn app-root []
  [view {:style {:flex 1
                 :align-items "center"
                 :justify-content "center"}}
   (doall (map item-view @(p/q '[:find [?e ...]
                                 :in $
                                 :where [?e :todo/description _]]
                               conn)))])


(defn init []
  (p/posh! conn)

  (.registerComponent ReactNative.AppRegistry "main"
                      #(r/reactify-component app-root)))


(comment

  (p/transact! conn [{:db/id -1
                      :todo/description "first task!"}])

  )

