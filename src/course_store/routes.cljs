(ns course-store.routes
  (:require
   [bidi.bidi :as bidi]
   [pushy.core :as pushy]
   [re-frame.core :as re-frame]
   [course-store.events :as events]))

(defmulti panels identity)
(defmethod panels :default [] [:div "No panel found for this route."])

(def routes
  (atom
    ["/" {""      :home
          "about" :about
          "course/" {[:id] :course}}]))

(defn parse
  [url]
  (bidi/match-route @routes url))

(defn url-for
  [args]
  (apply bidi/path-for @routes (:handler args) (mapcat identity (:route-params args))))

(defn dispatch
  [route]
  (let [panel (keyword (str (name (:handler route)) "-panel"))]
    (re-frame/dispatch [::events/set-active-panel {:panel panel :route-params (:route-params route)} ])))

(defonce history
  (pushy/pushy dispatch parse))

(defn navigate!
  [handler]
  (pushy/set-token! history (url-for handler)))

(defn start!
  []
  (pushy/start! history))

(re-frame/reg-fx
  :navigate
  (fn [handler]
    (navigate! handler)))
