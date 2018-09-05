(ns io.file-util
  (:require [clojure.java.io :as io]
            [clojure.string :as str])
  (:import (java.nio.file WatchService WatchKey Path FileSystems
                          StandardWatchEventKinds WatchEvent)
           (java.io File)))


(def info #'leiningen.core.main/info)
(def warn #'leiningen.core.main/warn)
(def ev-types {StandardWatchEventKinds/ENTRY_CREATE :create
               StandardWatchEventKinds/ENTRY_MODIFY :modify
               StandardWatchEventKinds/ENTRY_DELETE :delete})

(defn uri [filepath] (.toURI (io/file filepath)))

(defn standardize-filepath [path]
  (-> path
      (str/split #"\\|/")
      (->> (str/join "/"))))

(defn public-sass-file? [filename] (re-matches #"^.*(\\|/)[^_\\/]*\.(scss|sass)$" filename))

(defn public-sass-files [path]
  (->> path
       io/file
       file-seq
       (filter #(public-sass-file? (.getAbsolutePath %)))))

(defn gen-out-path [file src-path dest-path]
  (let [local-in (-> file
                     (.getAbsolutePath)
                     standardize-filepath
                     (str/split (re-pattern src-path))
                     last)
        local-name (str/join "." (butlast (str/split local-in #"\.")))]
    {:file file
     :out-path (str dest-path local-name ".css")}))

(defn handle-event [^WatchEvent ev ^Path dir dest-path handler]
  (when-not (= StandardWatchEventKinds/OVERFLOW
               (.kind ev))
    (let [fname (.context ev)
          child (.resolve dir ^Path fname)]
      (info "Change detected... Recompiling...")
      (info (-> child (.toFile) (.getAbsolutePath)))
      (handler (-> dir (.toFile) (.getAbsolutePath) standardize-filepath)
               dest-path))))

(defn watch-path [src-path dest-path handler]
  (info (str "Watching " src-path " for changes..."))
  (let [service (.. FileSystems getDefault newWatchService)
        watch-path (.toPath ^File (io/as-file src-path))
        watch-key (.register ^Path watch-path
                             service
                             (into-array (keys ev-types)))]
    (loop []
      (let [nk (.take service)
            _ (Thread/sleep 50)         ;; Delay to prevent repeated events for single file change
            events (.pollEvents nk)]
        (when events
          (handle-event (first events) watch-path dest-path handler))
        (when (.reset nk)
          (recur))))))