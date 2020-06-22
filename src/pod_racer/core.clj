(ns pod-racer.core
  (:require
    [clojure.edn :as edn]

    [bencode.core :as bencode]
    )
  (:import
    (java.io PushbackInputStream)
    ))

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


;; Config parsing

(defn ns->describe
  [pod-ns]
  {"name" (:pod/ns pod-ns)
   "vars" (mapv #(hash-map "name" (:var/name %)) (:pod/vars pod-ns))})

(defn pod-config->describe-map
  [pod-config]
  {"format" "edn"
   "namespaces" (mapv ns->describe (:pod/namespaces pod-config))})

(defn pod-config->fn-lookup
  [pod-config]
  (->> (for [ns (:pod/namespaces pod-config)
             :let [ns-name (:pod/ns ns)]
             var (:pod/vars ns)
             :let [{:var/keys [name fn]} var]]
         [(str ns-name "/" name) fn])
       (into {})))



;; I/O accounting

(defn format-exception
  [encode-fn e id]
  {"ex-message" (ex-message e)
   "ex-data" (encode-fn
               (assoc (ex-data e)
                      :type (class e)))
   "id" id
   "status" ["done" "error"]})

(defn format-result
  [encode-fn result id]
  {"value" (encode-fn result)
   "id" id
   "status" ["done"]})

(defn decode-message
  [message decode-fn]
  (let [{:strs [op id var args]} message]
    {:op (-> op bytes->str keyword)
     :id (some-> id bytes->str (or "unknown"))
     :var (some-> var bytes->str)
     :args (some-> args bytes->str decode-fn)}))


;; Launch function

(defn launch
  "Launch pod using the supplied config. Config is a map describing pod
  behavior. Example:

  {:pod/namespaces
   [{:pod/ns \"pod.math\"
     :pod/vars [{:var/name \"add\"
                 :var/fn +
                {:var/name \"subtract\"
                 :var/fn -}]}]}
  "
  [pod-config]
  (let [describe-map (pod-config->describe-map pod-config)
        fn-lookup (pod-config->fn-lookup pod-config)
        in (PushbackInputStream. System/in)
        [encode-fn decode-fn] [pr-str edn/read-string]]
    (loop []
      (when-some [message (try (read-ben in) (catch java.io.EOFException _e nil))]
        (let [{:keys [op id var args]} (decode-message message decode-fn)]
          (case op
            :describe (do (write-ben describe-map)
                          (recur))
            :invoke (do (try
                          (let [invoke-fn (fn-lookup var)
                                result (apply invoke-fn args)]
                            (write-ben (format-result encode-fn result id)))
                          (catch Throwable e
                            (write-ben (format-exception encode-fn e id))))
                        (recur))))))))
