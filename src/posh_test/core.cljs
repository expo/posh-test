(ns posh-test.core
  (:require [reagent.core :as r]))

(def ReactNative (js/require "react-native"))

(def text (r/adapt-react-class (.-Text ReactNative)))
(def view (r/adapt-react-class (.-View ReactNative)))

(defn app-root []
  [view {:style {:flex 1
                 :align-items "center"
                 :justify-content "center"}}
   [text "hello, world"]])

(defn init []
  (.registerComponent ReactNative.AppRegistry "main" #(r/reactify-component app-root)))

(comment

  (alert "hello")

  )
