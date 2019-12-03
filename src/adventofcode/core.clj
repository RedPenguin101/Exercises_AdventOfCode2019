(ns adventofcode.core
  (:require [adventofcode.fuel-calculator :as fuel]
            [adventofcode.intcode-emulator :as intcode]))

(defn file->vec [filename]
  (vec (map #(Integer/parseInt %) (clojure.string/split (slurp filename) #"\n"))))

(defn calc-fuel [filename]
  (fuel/total-fuel-required (file->vec filename)))

(defn -main
  [& args]
  (println "---Day 2.1---")
  (println (intcode/run-program 12 2 "intcode.txt"))
  (println "---Day 2.2---")
  (println (intcode/find-output 19690720 "intcode.txt")))