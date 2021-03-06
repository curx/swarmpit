(ns swarmpit.component.volume.info
  (:require [material.component :as comp]
            [material.icon :as icon]
            [swarmpit.url :refer [dispatch!]]
            [swarmpit.component.handler :as handler]
            [swarmpit.component.message :as message]
            [swarmpit.routes :as routes]
            [rum.core :as rum]))

(enable-console-print!)

(defn- delete-volume-handler
  [volume-name]
  (handler/delete
    (routes/path-for-backend :volume-delete {:name volume-name})
    {:on-success (fn [_]
                   (dispatch!
                     (routes/path-for-frontend :volume-list))
                   (message/info
                     (str "Volume " volume-name " has been removed.")))
     :on-error   (fn [response]
                   (message/error
                     (str "Volume removing failed. Reason: " (:error response))))}))

(rum/defc form < rum/static [item]
  [:div
   [:div.form-panel
    [:div.form-panel-left
     (comp/panel-info icon/volumes
                      (:volumeName item))]
    [:div.form-panel-right
     (comp/mui
       (comp/raised-button
         {:onTouchTap #(delete-volume-handler (:volumeName item))
          :label      "Delete"}))]]
   [:div.form-view
    [:div.form-view-group
     (comp/form-item "NAME" (:volumeName item))
     (comp/form-item "DRIVER" (:driver item))
     (comp/form-item "SCOPE" (:scope item))
     (comp/form-item "MOUNTPOINT" (:mountpoint item))]]])