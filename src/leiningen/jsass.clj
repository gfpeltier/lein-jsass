(ns leiningen.jsass
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [io.file-util :as files])
  (:import (java.io File)
           (io.bit3.jsass Options
                          Output
                          CompilationException)))


(def info #'leiningen.core.main/info)
(def warn #'leiningen.core.main/warn)

(defn compile-file! [{:keys [file out-path]}]
  (let [comp (io.bit3.jsass.Compiler.)
        opts (Options.)
        out (.compileFile comp
                          (.toURI file)
                          (files/uri out-path)
                          opts)]
    (info (str "Writing: " out-path))
    (io/make-parents (io/as-file out-path))
    (spit out-path (.getCss out))
    (spit (str out-path ".map") (.getSourceMap out))))

(defn compile-once
  "Compiles SASS at source path once and places result in target."
  [source target]
  (info "Compiling SASS files...")
  (try
    (let [sass-files (files/public-sass-files source)
          sass-maps (map #(files/gen-out-path % source target) sass-files)]
      (doseq [m sass-maps] (compile-file! m))
      (info "Compilation complete!"))
    (catch CompilationException e
      (warn "ERROR: An error occurred while compiling SASS files")
      (warn (.toString e)))))

(defn compile-auto
  "Watches source path and recompiles automatically when change detected."
  [source target]
  (compile-once source target)
  (info "Waiting for file changes...")
  (files/watch-path source target #'compile-once))

(defn jsass
  "Compiles sass to css. Expects project map and 'mode' string."
  {:subtasks [#'compile-once #'compile-auto]}
  [project & [mode]]
  (let [{:keys [source target]} (:jsass project)]
    (case mode
      "once" (compile-once source target)
      "auto" (compile-auto source target)
      (warn "Unknown mode. Use either 'once' or 'auto'. Aborting..."))))
