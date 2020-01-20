(ns adventofcode.gravity-sim)

(comment
  "A body is represented as a map of position vector and velocity vector"
  {:position [0 0 0] :velocity [1 1 1]})

(defn update-position [{:keys [position velocity] :as body}]
  (assoc body :position (map #(+ %1 %2) position velocity)))

(defn vel-modifier [a b]
  (let [test (- a b)]
    (cond
      (zero? test) identity
      (pos? test)  dec
      (neg? test)  inc)))


(defn update-velocity 
  "return vec of functions to apply to a velocity vector"
  [body1 body2]
  (assoc body1 :velocity (map #(%1 %2) 
                              (map #(vel-modifier %1 %2) (:position body1) (:position body2)) 
                              (:velocity body1))))

(comment
  (update-velocity {:position [0 0 0] :velocity [0 0 0]}
                   {:position [1 0 0] :velocity [0 0 0]})
  ;; => (1 0 0)

  )

(defn update-velocities [bodies]
  (for [body bodies]
    (reduce update-velocity body bodies)))

(def bodies [{:position [0 0 0] :velocity [0 0 0]}
             {:position [1 0 0] :velocity [0 0 0]}])
