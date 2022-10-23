# bb-pod-racer

Speed up development of Babashka [pods](https://github.com/babashka/pods).

[![Clojars Project](https://img.shields.io/clojars/v/org.endot/bb-pod-racer.svg)](https://clojars.org/org.endot/bb-pod-racer)

# Overview

This little library takes care of the overhead of communicating with Babashka.

# Example

Specify the namespaces and functions that should be exposed to Babashka scripts
in a `pod-config` and call `pod-racer.core/launch`. For example, with this pod
namespace:

```clojure
(ns pod.main
  (:require [pod-racer.core :as pod]))

(defn pod-fun
  [num]
  (inc num))

(def pod-config
  {:pod/namespaces
   [{:pod/ns "pod.example"
     :pod/vars [{:var/name "pod-fun"
                 :var/fn pod-fun}]}]})

(defn -main [& _args]
  (pod/launch pod-config))
```

And this Babashka script:

```clojure
#!/usr/bin/env bb

(require '[babashka.pods :as pods])

(pods/load-pod ["clojure" "-M" "-m" "pod.main"])

(require '[pod.example :as example])

(example/pod-fun 1)
```

The result will be:

```
$ ./pod_test.clj
2
```

# Projects using bb-pod-racer

* [tabl](https://github.com/justone/tabl) - Make tables from data in your terminal
* [brisk](https://github.com/justone/brisk) - Freeze and thaw with Nippy at the command line

# Caveats

No support (yet) for:
* Lazy loading of namespaces
* Async functions

# License

Copyright © 2020-2022 Nate Jones

Distributed under the EPL License. See LICENSE.
