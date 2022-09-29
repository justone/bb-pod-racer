(ns pod-racer.writer
  (:require [pod-racer.bencode :as bencode])
  (:gen-class :name pod-racer.PodWriter
              :extends java.io.Writer
              :state state
              :init init
              :constructors {[String] []}))

(defn -init
  [id]
  [[] id])

(defn -close
  [_this])

(defn -flush
  [_this]
  ;; Noop, as print flushes on Babashka side
  )

(defn -write
  ([this char-arr]
   (-write this char-arr 0 (count char-arr)))
  ([this char-arr offset len]
   (let [id (.state this)
         string (-> (vec char-arr)
                    (subvec offset (+ offset len))
                    (char-array)
                    (String.))]
     (bencode/write-ben {"id" id "out" string}))))
