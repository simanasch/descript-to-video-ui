(ns app.main.client
  (:require [chord.client :refer [ws-ch]]
            [cljs.core.async :refer [<! >! put! close!]])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(def channel (ws-ch "ws://localhost:3000/ws"))

(defn send-message-to-server [mes]
  (go
    (let [{:keys [ws-channel error]} (<! channel)]
      (if-not error
        (>! ws-channel mes)
        (js/console.log "Error:" (pr-str error))))))

(defn echo-test [mes]
  (go
    (let [{:keys [ws-channel error]} (<! (ws-ch "ws://localhost:3000/ws"))]
      (if-not error
        (do (>! ws-channel mes)
            (let [{:keys [message error]} (<! ws-channel)]
              (if-not error
                (do
                  (println "Got:" message)
                  (close! ws-channel))
                (println "Error:" (pr-str error)))))
        (println "Error:" (pr-str error))))))
(comment
  ;; async/goでwebsocketを通じサーバーにメッセージを送る
  (go
    (let [{:keys [ws-channel error]} (<! (ws-ch "ws://localhost:3000/ws"))]
      (if-not error
        (>! ws-channel "hello server")
        (js/console.log "Error:" (pr-str error)))))
  ()
  (go
    (let [{:keys [ws-channel error]} (<! channel)]
      (if-not error
        (>! ws-channel "hello server4")
        (js/console.log "Error:" (pr-str error)))))
  (send-message-to-server "hello from client2!")
  (go
    (let [{:keys [ws-channel]} (<! (ws-ch "ws://localhost:3000/ws"))
          {:keys [message]} (<! ws-channel)]
      (js/console.log "Got message from server:" (pr-str message))))

  (defn echo-test []
    (go
      (let [{:keys [ws-channel error]} (<! (ws-ch "ws://localhost:3000/ws"))]
        (if-not error
          (do (>! ws-channel "Hello server from client!")
              (let [{:keys [message error]} (<! ws-channel)]
                (if-not error
                  (do
                    (println "Got:" message)
                    (close! ws-channel))
                  (println "Error:" (pr-str error)))))
          (println "Error:" (pr-str error))))))
  (echo-test))