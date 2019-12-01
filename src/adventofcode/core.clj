(ns adventofcode.core)

(defn- fuel-one-level [mass]
  (max 0 (- (quot mass 3) 2)))

(defn fuel-required [mass]
  (reduce + (rest (take-while #(> % 0) (iterate fuel-one-level mass)))))

(defn total-fuel-required [module-masses]
  (reduce + (map fuel-required module-masses)))

(defn file->vec [filename]
  (vec (map #(Integer/parseInt %) (clojure.string/split (slurp filename) #"\n"))))

(defn calc-fuel [filename]
  (total-fuel-required (file->vec filename)))
