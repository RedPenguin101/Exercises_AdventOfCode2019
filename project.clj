(defproject adventofcode "0.0.1-SNAPSHOT"
  :description "Advent of Code 2019 challenges"
  :main ^:skip-aot adventofcode.core
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/core.async "0.6.532"]]
  :profiles {:dev {:dependencies [[midje "1.9.9"] [org.clojure/math.combinatorics "0.1.6"]]}
             :midje {}})

  
