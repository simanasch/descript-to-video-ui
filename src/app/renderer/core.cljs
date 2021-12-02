(ns app.renderer.core
  (:require [reagent.core :as r]
            [reagent.dom :as rd]
            [app.main.client :as client]
            ;; [app.main.backend :refer [get-tts-server-path] :as backend]
            ))

(enable-console-print!)

;; (defonce state (r/atom 0))
;; (defonce form-state (r/atom '{:value "test"}))

(defn form-component []
  (let [other-form-state (r/atom {:markdown-path "" :exo-path ""})
        ;; handle-change #(swap! other-form-state assoc :value %)
        handle-file-change (fn [key tgt] 
                            ;;  (js/console.log (-> tgt .-target .-value))
                             (if (not (= "" (-> tgt .-target .-value)))
                               (swap! other-form-state assoc (keyword key) (-> tgt .-target .-files (aget 0) .-path))
                               nil))]
    (fn []
      [:<>
       [:h3 "対象markdownの選択画面"]
       [:div
        [:label {:for "exo-file"} (str "テンプレにする.exoファイル:" (:exo-path @other-form-state))]
        [:input#exo-file {:type "file"
                          :title "テンプレにする.exoファイル"
                          :name  "exo-file"
                          :on-change #(handle-file-change "exo-path" %)}]]

       [:div
        [:label {:for "markdown-file"} (str "スライドとttsを出力するmarkdown:" (:markdown-path @other-form-state))]
        [:input#markdown-file {:type "file"
                               :title "変換対象のmarkdown"
                               :name  "markdown-file"
                               :on-change #(handle-file-change "markdown-path" %)}]]
      ;;  [:div
      ;;   [:label (:sample @other-form-state)]
      ;;   [:input {:on-change #(handle-change
      ;;                         (.. % -target -value))}]]

       [:button.button-primary {:on-click #(if-not (empty? @other-form-state) (client/send-message-to-server (str @other-form-state)))} (str "backendに送る")]])))

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
