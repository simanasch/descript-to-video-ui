(ns app.renderer.core
  (:require [reagent.core :as r]
            [reagent.dom :as rd]))

(enable-console-print!)

(defonce state (r/atom 0))
;; (defonce form-state (r/atom '{:value "test"}))

(defn form-component []
  (let [other-form-state (r/atom {:value "test" :sample ""})
        handle-change #(swap! other-form-state assoc :value %)
        handle-file-change (fn [tgt] 
                             (js/console.log (-> tgt .-target .-value))
                             (if (not (= "" (-> tgt .-target .-value)))
                               (swap! other-form-state assoc :path (-> tgt .-target .-files (aget 0) .-path))
                               nil))]
    (fn []
      [:<>
       [:form
        [:div>label "対象markdownの選択画面"]
        [:div
         [:label {:to "markdown-file"} (:path @other-form-state)]
         [:input#markdown-file {:type "file" 
                                :title "変換対象のmarkdown" 
                                :name  "markdown-file"
                                :on-change #(handle-file-change %)}]
         ]
        [:div
         [:label (:sample @other-form-state)]
         [:input {:on-change #(handle-change
                               (.. % -target -value))}]]
        ;; [:button
        ;;  {:on-click #(js/alert "click")}
        ;;  (str "submit")]
        ]
      ;;  [:label (:value @other-form-state)]
      ;;  [:button {:on-click #(swap! other-form-state assoc :value "clicked!!!!!!")} (str "sample")]
       ])))

(defn root-component []
  [:div
  ;;  [:div.logos
  ;;   [:img.electron {:src "img/electron-logo.png"}]
  ;;   [:img.cljs {:src "img/cljs-logo.svg"}]
  ;;   [:img.reagent {:src "img/reagent-logo.png"}]]
  ;;  [:button
  ;;   {:on-click #(swap! state inc)}
  ;;   (str "Clicked " @state " times")]
   [form-component]
   ])

(defn ^:dev/after-load start! []
  (rd/render
   [root-component]
   (js/document.getElementById "app-container")))
