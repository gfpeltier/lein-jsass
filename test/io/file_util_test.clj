(ns io.file-util-test
  (:require [clojure.test :refer :all]
            [io.file-util :as files]))

(def rel-win-pub-path "some\\awesome\\file.scss")
(def rel-win-pri-path "my\\awesome\\_file.scss")
(def abs-win-pub-path "C:\\asdf\\edv\\_ij_-384\\aha.scss")
(def abs-win-pri-path "C:\\asdf\\edv\\_ij_-384\\_aha.scss")

(def rel-unix-pub-path "some/awesome/file.scss")
(def rel-unix-pri-path "my/awesome/_file.scss")
(def abs-unix-pub-path "/home/user/_awesome/file.scss")
(def abs-unix-pri-path "/home/user/_awesome/_file.scss")

(deftest standardize-file-path-tests
  (is (= rel-unix-pub-path (files/standardize-filepath rel-win-pub-path)))
  (is (= rel-unix-pub-path (files/standardize-filepath rel-unix-pub-path)))
  (is (= rel-unix-pri-path (files/standardize-filepath rel-win-pri-path)))
  (is (= rel-unix-pri-path (files/standardize-filepath rel-unix-pri-path)))

  (is (= "C:/asdf/edv/_ij_-384/aha.scss" (files/standardize-filepath abs-win-pub-path))))

(deftest public-sass-file?-tests
  (is (files/public-sass-file? rel-win-pub-path))
  (is (files/public-sass-file? abs-win-pub-path))
  (is (files/public-sass-file? rel-unix-pub-path))
  (is (files/public-sass-file? abs-unix-pub-path))

  (is (not (files/public-sass-file? rel-win-pri-path)))
  (is (not (files/public-sass-file? abs-win-pri-path)))
  (is (not (files/public-sass-file? rel-unix-pri-path)))
  (is (not (files/public-sass-file? abs-unix-pri-path))))