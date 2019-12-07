(ns adventofcode.core
  (:require [adventofcode.fuel-calculator :as fuel]
            [adventofcode.intcode-emulator :as intcode]
            [adventofcode.manhattan-calc :as m-calc]
            [adventofcode.password-crack :as pwd]
            [adventofcode.orbit-checksum :as orbit]
            ))

(defn file->vec [filename]
  (vec (map #(Integer/parseInt %) (clojure.string/split (slurp filename) #"\n"))))

(defn calc-fuel [filename]
  (fuel/total-fuel-required (file->vec filename)))

(defn -main
  [& args]
  ;;(println "---Day 2.1---")
  ;;(println (intcode/run-program 12 2 "intcode.txt"))
  
  ;;(println "---Day 2.2---")
  ;;(println (intcode/find-output 19690720 "intcode.txt"))
  
  ;;(println "---Day 3.1---")
  ;;(def paths (clojure.string/split-lines (slurp "day3.txt")))
  ;;(def path1 (m-calc/build-path (paths 0)))
  ;;(def path2 (m-calc/build-path (paths 1)))
  ;;(println (m-calc/min-man-distance (m-calc/find-intersections path1 path2)))
  
  ;;(println "---Day 3.2---")
  ;;(println (m-calc/min-path-distance path1 path2))
  
  ;;(println "---Day 4.2---")
  ;;(println (count (filter pwd/valid-code? (range 123257 647016))))
  ;;(println "---Day 5.1---")
  ;;(intcode/run (intcode/load-memory-state "day5.txt"))

  (println "=== Day 6.1 ===")
  (orbit/orbit-checksum (orbit/load-orbit-file "day6.txt"))
  )
