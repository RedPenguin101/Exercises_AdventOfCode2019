(defproject adventofcode "0.0.1-SNAPSHOT"
  :description "Advent of Code 2019 challenges"
  :main ^:skip-aot adventofcode.core
  :dependencies [[org.clojure/clojure "1.10.1"]]
  :profiles {:dev {:dependencies [[midje "1.9.9"]]}
             ;; You can add dependencies that apply to `lein midje` below.
             ;; An example would be changing the logging destination for test runs.
             :midje {}})
             ;; Note that Midje itself is in the `dev` profile to support
             ;; running autotest in the repl.

  
