(ns adventofcode.sif-decoder)

(defn string->layers [string x-size y-size]
  (partition (* x-size y-size) (map #(Character/digit % 10) string)))


