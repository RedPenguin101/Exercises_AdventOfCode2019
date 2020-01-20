(ns adventofcode.gravity-sim)

(comment
  "A body is represented as a map of position vector and velocity vector"
  {:position [0 0 0] :velocity [1 1 1]})

(defn update-position [{:keys [position velocity] :as body}]
  (assoc body :position (map #(+ %1 %2) position velocity)))

(defn vel-modifier [a b]
  (cond
    (zero? (- a b)) identity
    (pos? (- a b))  dec
    (neg? (- a b))  inc))


(defn update-velocity 
  "Given two bodies, modifies the velocity vector of th first one to reflect
  the impact of gravity between the two bodies"
  [body1 body2]
  (assoc body1 
         :velocity 
         (vec (map #(%1 %2) 
                   (map #(vel-modifier %1 %2) (:position body1) (:position body2)) 
                   (:velocity body1)))))

(defn update-velocities [bodies]
  (for [body bodies]
    (reduce update-velocity body bodies)))
