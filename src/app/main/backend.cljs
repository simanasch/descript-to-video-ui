(ns app.main.backend
  ;; (:require-macros [cljs.core.async.macros :refer [go alt!]])
  (:require [cljs.core.async :refer [<! take! chan close! put! go-loop >!] :as async]
            [clojure.string :refer [join]]
            ["child_process" :refer [execFile] :as child_process]
            ;; [cljs.nodejs :refer [process]]
            ))
;; (defn get-resources-path [^process p] (.-resourcesPath p))
(defonce tts-server-process (atom nil))
(defonce core-server-process (atom nil))


(defn get-tts-server-path
  [isPackaged]
  (cond (not isPackaged)
        "./bin/SpeechGRpcServer/SpeechGRpcServer.exe"
        :else (join "/" [
                        ;;  (get-resources-path process) 
                         "."
                         "resources" "bin" "SpeechGRpcServer" "SpeechGRpcServer.exe"])))

(defn get-core-server-path
  [isPackaged]
  (cond (not isPackaged)
        "./bin/descriptToVideo-0.1.0-SNAPSHOT-standalone.jar"
        :else (join "/" [
                        ;;  (get-resources-path process)
                         "."
                         "resources" "bin" "descriptToVideo-0.1.0-SNAPSHOT-standalone.jar"])))

(defn kill-process [^child_process x]
  (do
    (.kill x)
    (js/console.info "exitcode,pid,spawnfile,isPackaged,resourcespath\n" (.-exitCode x) (.-pid x) (.-spawnfile x))))

(defn start-tts-server
  [isPackaged]
  (let [p (execFile (get-tts-server-path isPackaged))]
    (js/console.log "tts server:" (.-exitCode p) (.-pid p) (.-spawnfile p) (get-tts-server-path isPackaged) isPackaged)
    (reset! tts-server-process p)))

(defn stop-tts-server
  []
  (do
    (kill-process @tts-server-process)
    (reset! tts-server-process nil)))

(defn start-core-server
  [isPackaged]
  (let [p (execFile "java" (clj->js ["-jar" (get-core-server-path isPackaged)]))]
    (js/console.log "core server:" (.-exitCode p) (.-pid p) (.-spawnfile p) (get-core-server-path isPackaged))
    (reset! core-server-process p)))

(defn stop-core-server
  []
  (do
    (kill-process @core-server-process)
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
        (.on proc "close" #(put! channel [:done %]))))))

(comment
  (def proc (atom (main!)))
  (.kill @proc))