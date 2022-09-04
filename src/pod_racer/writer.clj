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
  [this]
  ; (binding [*out* *err*]
  ;   (prn :flush))
  (bencode/write-ben {"id" (.state this) "flush" ""}))

(defn -write
  ([this char-arr]
   (-write this char-arr 0 (count char-arr)))
  ([this char-arr offset len]
   (let [id (.state this)
         string (-> (vec char-arr)
                    (subvec offset (+ offset len))
                    (char-array)
                    (String.))]
     ; (binding [*out* *err*]
     ;   (prn :ingredients char-arr offset len)
     ;   (prn :final id)
     ;   (prn :print (str "|" string "|")))
     (bencode/write-ben {"id" id
                         "print" string}))))
