(ns build
  (:require
    [clojure.tools.build.api :as b]
    [org.corfield.build :as cb]))

; (def lib 'org.endot/bb-pod-racer)
; (def version (format "1.0.%s" (b/git-count-revs nil)))

(def basis (b/create-basis {:project "deps.edn"}))
(def class-dir "target/classes")

(def clean cb/clean)

(defn prep
  [_]
  (b/compile-clj {:basis basis
                  :src-dirs ["src"]
                  :ns-compile ['pod-racer.writer]
                  :class-dir class-dir}))
