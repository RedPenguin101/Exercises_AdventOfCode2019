(ns adventofcode.core
  (:require [adventofcode.fuel-calculator :as fuel]
            [adventofcode.intcode-emulator :as intcode]))

(defn file->vec [filename]
  (vec (map #(Integer/parseInt %) (clojure.string/split (slurp filename) #"\n"))))

(defn calc-fuel [filename]
  (fuel/total-fuel-required (file->vec filename)))
