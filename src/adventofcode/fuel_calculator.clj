(ns adventofcode.fuel-calculator)

(defn- fuel-one-level [mass]
  (max 0 (- (quot mass 3) 2)))

(defn fuel-required [mass]
  (reduce + (rest (take-while pos? (iterate fuel-one-level mass)))))

(defn total-fuel-required [module-masses]
  (reduce + (map fuel-required module-masses)))

