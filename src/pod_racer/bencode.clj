(ns pod-racer.bencode
  (:require [bencode.core :as bencode]))

;; Bencode helpers

(defn read-ben
  [in]
  (bencode/read-bencode in))

(defn write-ben
  [data]
  (bencode/write-bencode System/out data)
  (.flush System/out))

(defn bytes->str [^"[B" v]
  (String. v))
