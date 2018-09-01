(ns leiningen.jsass
  (:require [clojure.java.io :as io]
            [clojure.string :as str])
  (:import (java.io File)
           (io.bit3.jsass Options
                          Output
                          CompilationException)))


(def info #'leiningen.core.main/info)
(def warn #'leiningen.core.main/warn)

(defn uri [filepath] (.toURI (io/file filepath)))

(defn standardize-filepath [path]
  (-> path
      (str/split #"\\|/")
      (->> (str/join "/"))))

(defn public-sass-file? [filename] (re-matches #"^.*\\[^_\\]*\.(scss|sass)$" filename))

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

(defn compile-file! [{:keys [file out-path]}]
  (let [comp (io.bit3.jsass.Compiler.)
        opts (Options.)
        out (.compileFile comp
                          (.toURI file)
                          (uri out-path)
                          opts)]
    (info (str "Writing: " out-path))
    (io/make-parents (io/as-file out-path))
    (spit out-path (.getCss out))
    (spit (str out-path ".map") (.getSourceMap out))))

(defn jsass
  "Compiles sass to css."
  [project & args]
  (info "Compiling SASS files...")
  (try
    (let [{:keys [source target]} (:jsass project)
          sass-files (public-sass-files source)
          sass-maps (map #(gen-out-path % source target) sass-files)]
      (doseq [m sass-maps] (compile-file! m))
      (info "CSS compilation complete."))
    (catch CompilationException e
      (warn "ERROR: An error occurred while compiling sass files")
      (warn (.toString e)))))
