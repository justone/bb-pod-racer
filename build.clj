(ns build
  "Build script.

  clojure -T:build jar
  clojure -T:build deploy

  Add `:snapshot true` args to the above
  to create/deploy snapshot version.

  Run tests via:
  clojure -X:test

  For more information, run:
  clojure -A:deps -T:build help/doc"
  (:require
    [clojure.tools.build.api :as b]
    [org.corfield.build :as cb]))

(def lib 'org.endot/bb-pod-racer)
(defn- the-version [patch] (format "0.1.%s" patch))
(def version (the-version (b/git-count-revs nil)))
(def snapshot (the-version "999-SNAPSHOT"))

(def clean
  "Clean project"
  cb/clean)

(defn prep
  "Prep project by compiling necessary classes."
  [opts]
  (b/compile-clj {:basis (b/create-basis {})
                  :ns-compile ['pod-racer.writer]
                  :class-dir (cb/default-class-dir)})
  opts)

(defn jar
  "Run the CI pipeline of tests (and build the JAR)."
  [opts]
  (-> opts
      (assoc :lib lib :version (if (:snapshot opts) snapshot version))
      (cb/run-tests)
      (cb/clean)
      (prep)
      (cb/jar)))

(defn install
  "Install the JAR locally."
  [opts]
  (-> opts
      (assoc :lib lib :version (if (:snapshot opts) snapshot version))
      (cb/install)))

(defn deploy
  "Deploy the JAR to Clojars."
  [opts]
  (-> opts
      (assoc :lib lib :version (if (:snapshot opts) snapshot version))
      (cb/deploy)))
