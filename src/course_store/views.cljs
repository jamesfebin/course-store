(ns course-store.views
  (:require
   [re-frame.core :as re-frame]
  [re-frame.core :refer [reg-event-db reg-event-fx reg-fx inject-cofx trim-v after path reg-sub subscribe dispatch-sync dispatch]]
   [re-com.core :as re-com :refer [at]]
  ;;  [course-store.styles :as styles]
   [course-store.events :as events]
   [course-store.routes :as routes]
   [course-store.subs :as subs]))

(def courses-list [
              {:id 1 :name "Introduction to Clojure" :description "Learn to build powerful applications with less code using Clojure." :category "Clojure" :started false}
              {:id 2 :name "ClojureScript Basics" :description "Build frontend application at lightning speeds." :category "Clojure" :started false}
              {:id 3 :name "Rust Basics" :description "Run frontend application at lightning speeds." :category "Rust" :started false}
              {:id 4 :name "Rust Advanced" :description "Build great games at lightning speeds." :category "Rust" :started true}
              ])

(reg-event-db
  :initialize-db
  (fn [db event]
    (println "DB Started")
    (assoc db :courses courses-list :filters [])))



;; (reg-event-db
;;  :add-filter
;;  (fn [db [event filter]]
;;    (assoc db :filters (conj (get-in db [:filters]) filter))))

(reg-event-db
 :init-filter
 (fn [db [_ filter]]
   (println "Initializing filter")
   (println (assoc {:filters []} :filters [filter]))
   (assoc db :filters [filter])
   ))

;; (def filters [#(= "Rust" (:category %)) #(= false (:started %))])
;; ;; (def filters [])

(defn generate-predicates 
  [condition]
  '(= (:value condition) ((keyword (:key condition)) %))
  )

(defn build-pred-vec
  [[filter]]
  (fn [item]
    ((:condition filter) ((keyword (:field filter)) item) (:value filter))))

;; (filter (build-pred-vec [{:condition = :field "category" :value "Clojure"}]) courses-list)

(reg-sub
 :courses
 (fn [db _]
   (let [filters (get-in db [:filters])
         courses (get-in db [:courses])]
     (cond
       (empty? filters) courses
       :else (filter (build-pred-vec filters) courses)))))

;; (reg-sub
;;  :courses 
;;  (fn [db _]
;;    (let [predicates (map generate-predicates (get-in db [:filters]))]
;;      (println "predicates")
;;      (println predicates)
;;     (cond 
;;      (empty? predicates) (get-in db [:courses])
;;      :else (filter (apply every-pred filters) (get-in db [:courses]))))))

(defn navbar []
  [:nav {:class "navbar"
         :role "navigation"
         :aria-label "main navigation"}
   [:div {:class "navbar-brand"}
    [:a {:class "navbar-item"}
     [:img {:src "https://bulma.io/images/bulma-logo.png"}]]]
   [:div {:class "navbar-menu"}
    [:div {:class "navbar-start"}
    [:a {:class "navbar-item"}
     "Courses"]]
    [:div {:class "navbar-end"}
      [:a {:class "navbar-item"}
       "My Learning"]
     [:a {:class "navbar-item"}
      [:div.field
       [:p.control.has-icons-left.has-icons-right
        [:input.input.is-small {:placeholder "Search Courses", :type "text"}]
        [:span.icon.is-small.is-left [:i.zmdi.zmdi-search]]]]]
     [:div {:class "navbar-item has-dropdown is-hoverable"}
      [:a {:class "navbar-link"}
       "Account"]
      [:div {:class "navbar-dropdown"}
       [:a {:class "navbar-item"}
        [:i {:class "zmdi zmdi-power"} " Logout"]]]]]]]) 

(defn course-menu []
  [:aside {:class "menu"}
   [:p {:class "menu-label"}
    "Categories"]
   [:ul {:class "menu-list"}
    [:li [:a {:on-click #(dispatch [:init-filter {:condition = :field "category" :value "Clojure"}] )} "Clojure"  ]]
    [:li [:a {:on-click #(dispatch [:init-filter {:condition = :field "category" :value "Rust"}])} "Rust" ]]]])

(defn course-card [course]
  [:div.column.is-4 {:key (:id course)}
   [:div.card  { :on-click #(re-frame/dispatch [::events/navigate {:handler :course :route-params {:id "abc"}}])}
   [:div.card-image
    [:figure.image.is-4by3
     [:img
      {:alt "Placeholder image"
       :src "https://bulma.io/images/placeholders/1280x960.png"}]]]
   [:div.card-content
    [:div.media
      [:div.media-content
      [:p.title.is-4 (:name course)]
      ]]
    [:div.content
     (:description course)
     ]
    [:button.button.is-link "Start Course"]]]])


(defn courses-panel []
  (let [courses-list @(subscribe [:courses])]
  [:div {:class "columns" :style {:padding "2%"}}
   [:div {:class "column"}
    (course-menu)]
   [:div {:class "column is-four-fifths"}
    [:div.columns.is-multiline
     (map course-card courses-list)]
     ]]))

(defn comment-input []
  [:div 
  [:textarea.textarea]
  [:br]
  [:button.button.is-link "Comment"]])

(defn comments []
  [:div
   [:div.columns
   [:div.column.is-2
    [:img
    {:src
     "https://avataaars.io/?avatarStyle=Circle&topType=LongHairStraight&accessoriesType=Blank&hairColor=BrownDark&facialHairType=Blank&clotheType=BlazerShirt&eyeType=Default&eyebrowType=Default&mouthType=Default&skinColor=Light"}]
    [:center "Sara"]]
   [:div.column
    [:br]
    [:br]
    [:i "Thanq, beautifully explained" ]]]
   (comment-input)])

(defn course-panel [params]
  [:div
   (navbar)
   [:div.columns {:style {:padding "2%"}}
    [:div {:class "column is-three-fifths"}
     [:h2.is-size-4  "Introduction to Clojure"]
     [:br]
     [:iframe
      {:allowfullscreen "allowfullscreen"
       :allow
       "accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
       :frameborder "0"
       :title "YouTube video player"
       :src "https://www.youtube.com/embed/BThkk5zv0DE"
       :height "315"
       :width "560"}]
     [:br]
     [:br]
     [:br]
     [:h2.is-size-5  "Comments"]
     [:hr]
     (comments)]
    [:div {:class "column"}
     [:br]
     [:br]
     [:aside {:class "menu"}
      [:p {:class "menu-label"}
       "Lessons"]
      [:ul {:class "menu-list"}
       [:li [:a "1. Why Clojure?"]]
       [:li [:a "2. Pure Functions"]]]]]]])

(defmethod routes/panels :course-panel [_ params] [course-panel params])

(defn home-title []
  [:div "Hello World"])

(defn link-to-about-page []
  [re-com/hyperlink
   :src      (at)
   :label    "go to About Page"
   :on-click #(re-frame/dispatch [::events/navigate {:handler :about}])])

(defn home-panel []
  [:div 
   (navbar)
   (courses-panel)])

(defmethod routes/panels :home-panel [] [home-panel])

;; about

(defn about-title []
  [re-com/title
   :src   (at)
   :label "This is the About Page."
   :level :level1])

(defn link-to-home-page []
  [re-com/hyperlink
   :src      (at)
   :label    "go to Home Page"
   :on-click #(re-frame/dispatch [::events/navigate {:handler :home}])])

(defn about-panel []
  [re-com/v-box
   :src      (at)
   :gap      "1em"
   :children [[about-title]
              [link-to-home-page]]])

(defmethod routes/panels :about-panel [] [about-panel])

;; main
(dispatch [:initialize-db])

(defn main-panel []
  (let [active-panel (re-frame/subscribe [::subs/active-panel])]
    (println @active-panel)
    [re-com/v-box
     :src      (at)
     :height   "100%"
     :children [(routes/panels (:panel @active-panel) (:route-params @active-panel))]]))
