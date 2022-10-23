(ns pod-racer.writer-test
  (:require [clojure.test :refer [ is deftest]]
            [pod-racer.bencode :as bencode])
  (:import [pod-racer PodWriter]))

(deftest prints
  (let [captures (atom [])]
    (with-redefs [bencode/write-ben #(swap! captures conj %)]
      (binding [*out* (PodWriter. "session-id")]
        (print "one")
        (print "two")
        (println "foo")))
    (is (= [{"id" "session-id", "out" "one"}
            {"id" "session-id", "out" "two"}
            {"id" "session-id", "out" "foo"}
            {"id" "session-id", "out" "\n"}]
           @captures))))

(deftest offsets
  (let [captures (atom [])
        test-string-chars (.toCharArray "The quick brown fox jumps over the lazy dog")]
    (with-redefs [bencode/write-ben #(swap! captures conj %)]
      (binding [*out* (PodWriter. "session-id")]
        (.write *out* test-string-chars 0 3)
        (.write *out* (int \a))
        (.write *out* test-string-chars 16 3)
        (.write *out* test-string-chars 40 3)))
    (is (= [{"id" "session-id", "out" "The"}
            {"id" "session-id", "out" "a"}
            {"id" "session-id", "out" "fox"}
            {"id" "session-id", "out" "dog"}]
           @captures))))

