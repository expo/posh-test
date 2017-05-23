(ns posh-test.core
  (:require [reagent.core :as r]
            [posh.reagent :as p]
            [datascript.core :as d]))


(def conn (d/create-conn {:movie/actors {:db/cardinality :db.cardinality/many
                                         :db/valueType :db.type/ref}
                          :movie/director {:db/valueType :db.type/ref}}))


(def ReactNative (js/require "react-native"))

(def text (r/adapt-react-class (.-Text ReactNative)))
(def view (r/adapt-react-class (.-View ReactNative)))

(defn app-root []
  [view {:style {:flex 1
                 :align-items "center"
                 :justify-content "center"}}
   (for [c @(p/q '[:find [?name ...]
                   :in $
                   :where [_ :person/name ?name]]
                 conn)]
     [text {:key c} c])])


(defn init []
  (p/posh! conn)

  (d/transact! conn [{:db/id -1
                      :person/name "Tom Cruise"}
                     {:db/id -2
                      :person/name "Anthony Edwards"}
                     {:db/id -3
                      :person/name "Tony Scott"}
                     {:db/id (d/tempid :user)
                      :movie/title "Top Gun"
                      :movie/year 1986
                      :movie/actors [-1 -2]
                      :movie/director -3}
                     {:db/id -4
                      :person/name "Arnold Schwarzenegger"}
                     {:db/id (d/tempid :user)
                      :movie/title "Terminator"
                      :movie/actors -4}
                     {:db/id -5
                      :person/name "Mel Brooks"}
                     {:db/id (d/tempid :user)
                      :movie/title "Spaceballs"
                      :movie/actors -5
                      :movie/director -5}
                     {:db/id -6
                      :person/name "Clint Eastwood"
                      :person/birth-year 1930}
                     {:db/id -7
                      :person/name "Morgan Freeman"}
                     {:db/id -8
                      :person/name "Gene Hackman"}
                     {:db/id -9
                      :person/name "Eli Wallach"}
                     {:db/id (d/tempid :user)
                      :movie/title "The Good, The Bad and The Ugly"
                      :movie/actors [-6 -9]}
                     {:db/id (d/tempid :user)
                      :movie/title "Unforgiven"
                      :movie/actors [-6 -7 -8]
                      :movie/director -6}])

  (.registerComponent ReactNative.AppRegistry "main" #(r/reactify-component app-root)))


(comment

  (let [clint (d/q '[:find ?e .
                     :in $
                     :where [?e :person/name "Clint Eastwood"]]
                   @conn)]
    (d/transact! conn [{:db/id clint
                        :person/name "Clint Eastwood Edited"}]))

  )

