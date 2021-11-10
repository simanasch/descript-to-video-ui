(ns app.main.client
  (:require [chord.client :refer [ws-ch]]
            [cljs.core.async :refer [<! >! put! close!]])
  (:require-macros [cljs.core.async.macros :refer [go]]))



(comment
  ;; async/goでwebsocketを通じサーバーにメッセージを送る
 (go
  (let [{:keys [ws-channel error]} (<! (ws-ch "ws://localhost:3000/ws"))]
    (if-not error
      (>! ws-channel "hello server2!")
      (js/console.log "Error:" (pr-str error))))))