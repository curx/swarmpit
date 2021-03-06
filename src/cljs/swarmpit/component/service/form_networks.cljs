(ns swarmpit.component.service.form-networks
  (:require [material.component :as comp]
            [swarmpit.component.state :as state]
            [swarmpit.routes :as routes]
            [rum.core :as rum]))

(enable-console-print!)

(def cursor [:page :service :wizard :networks])

(def headers [{:name  "Name"
               :width "300px"}
              {:name  "Driver"
               :width "200px"}])

(def empty-info
  (comp/form-value "Service is not connected to any networks."))

(defn- form-network [value index data]
  (comp/form-list-selectfield
    {:name     (str "form-network-select-" index)
     :key      (str "form-network-select-" index)
     :value    value
     :onChange (fn [_ _ v]
                 (state/update-item index :networkName v cursor))}
    (->> data
         (map #(comp/menu-item
                 {:name        (str "form-network-item-" (:networkName %))
                  :key         (str "form-network-item-" (:networkName %))
                  :value       (:networkName %)
                  :primaryText (:networkName %)})))))

(defn- render-networks
  [item index data]
  (let [{:keys [networkName]} item]
    [(form-network networkName index data)]))

(defn- form-table
  [networks data]
  (comp/form-table-headless [{:name  "Name"
                              :width "300px"}]
                            networks
                            data
                            render-networks
                            (fn [index] (state/remove-item index cursor))))

(def render-item-keys
  [[:networkName] [:driver]])

(defn- render-item
  [item network]
  (let [value (val item)]
    (case (key item)
      :networkName (comp/link
                     (routes/path-for-frontend :network-info (select-keys network [:id]))
                     value)
      value)))

(defn- add-item
  []
  (state/add-item {:networkName ""} cursor))

(rum/defc form-create < rum/reactive [data]
  (let [networks (state/react cursor)]
    [:div
     (comp/form-add-btn "Attach network" add-item)
     (if (not (empty? networks))
       (form-table networks data))]))

(rum/defc form-update < rum/static [networks]
  (if (empty? networks)
    empty-info
    (comp/form-table-ro headers
                        networks
                        render-item
                        render-item-keys)))

(rum/defc form-view < rum/static [networks]
  (if (empty? networks)
    empty-info
    (comp/form-info-table headers
                          networks
                          render-item
                          render-item-keys)))