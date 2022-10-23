(ns pod-racer.writer
  (:require [pod-racer.bencode :as bencode])
  (:gen-class :name pod-racer.PodWriter
              :extends java.io.Writer
              :state state
              :init init
              :constructors {[String] []})
  (:import [java.util Arrays]))

(set! *warn-on-reflection* true)

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
  ([this value]
   (let [char-arr (cond-> value
                    (string? value) char-array
                    (int? value) Character/toChars)]
     (-write this char-arr 0 (count char-arr))))
  ([this char-arr offset len]
   (let [id (.state ^pod-racer.PodWriter this)
         ^"[C" sub-char-arr (Arrays/copyOfRange ^"[C" char-arr (int offset) (int (+ offset len)))]
     (bencode/write-ben {"id" id "out" (String. sub-char-arr)}))))
