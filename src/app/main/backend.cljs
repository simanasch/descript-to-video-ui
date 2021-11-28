(ns app.main.backend
  (:require-macros [cljs.core.async.macros :refer [go alt!]])
  (:require [cljs.core.async :refer [<! take! chan close! put! go-loop >!] :as async])
  (:require ["child_process" :refer [execFile]]))

(defonce tts-server-process (atom nil))
(defonce core-server-process (atom nil))

(def tts-server-path "./resources/bin/SpeechGRpcServer/SpeechGRpcServer.exe")

(def core-server-path "./resources/bin/descriptToVideo-0.1.0-SNAPSHOT-standalone.jar")

(defn start-tts-server
  []
  (reset! tts-server-process 
          (execFile tts-server-path)))

(defn stop-tts-server
  []
  (do 
    (.kill @tts-server-process)
    (reset! tts-server-process nil)))

(defn start-core-server
  []
  (reset! core-server-process
          (execFile "java" (clj->js ["-jar" core-server-path]))))

(defn stop-core-server
  []
  (do
    (.kill @core-server-process)
    (reset! core-server-process nil)))

(comment
  (defn done-message? [message]
  (and
   (vector? message)
   (= (message 0) :done)))

  (defn main! []
    (let [channel (chan)]

      (go-loop []
        (let [v (<! channel)]
          (if (done-message? v)
            (do
              (println "closing channel")
              (close! channel))
            (do
              (println (.toString v "UTF8"))
            ;; (recur)
              ))
          (recur)))

      (let [proc (execFile tts-server-path)]
        (.on (.-stdout proc) "data" #(put! channel %))
        (.on (.-stderr proc) "data"  #(put! channel %))
        (.on proc "close" #(put! channel [:done %])))))
  )

(comment
  (def proc (atom (main!)))
  (.kill @proc))