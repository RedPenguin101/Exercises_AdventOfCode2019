(ns adventofcode.gravity-sim)

(comment
  "A body is represented as a map of position vector and velocity vector"
  {:position [0 0 0] :velocity [1 1 1]})

(defn- update-position [{:keys [position velocity] :as body}]
  (assoc body :position (vec (map #(+ %1 %2) position velocity))))

(defn- vel-modifier [a b]
  (cond
    (zero? (- a b)) identity
    (pos? (- a b))  dec
    (neg? (- a b))  inc))


(defn- update-velocity [body1 body2]
  (assoc body1 
         :velocity 
         (vec (map #(%1 %2) 
                   (map #(vel-modifier %1 %2) (:position body1) (:position body2)) 
                   (:velocity body1)))))

(defn- update-velocities [bodies]
  (for [body bodies]
    (reduce update-velocity body bodies)))

(defn step [bodies]
  (->> (update-velocities bodies)
      (map update-position)))

(comment
  "take multiple steps like"

  (def bodies [{:position [-1 0 2] :velocity [0 0 0]}
               {:position [2 -10 -7] :velocity [0 0 0]}
               {:position [4 -8 8] :velocity [0 0 0]}
               {:position [3 5 -1] :velocity [0 0 0]}])

  "this would give the positions after 10 steps"
  (last (take 11 (iterate step bodies)))
  ;; => ({:position (2 1 -3), :velocity [-3 -2 1]}
  ;;     {:position (1 -8 0), :velocity [-1 1 3]}
  ;;     {:position (3 -6 1), :velocity [3 2 -3]}
  ;;     {:position (2 0 4), :velocity [1 -1 -1]})

  )

(defn total-energy [{:keys [position velocity]}]
  (* (apply + (map #(Math/abs %) position)) (apply + (map #(Math/abs %) velocity))))

(comment 
  (reduce + (map total-energy [{:position [2 1 -3], :velocity [-3 -2 1]}
                               {:position [1 -8 0], :velocity [-1 1 3]}
                               {:position [3 -6 1], :velocity [3 2 -3]}
                               {:position [2 0 4], :velocity [1 -1 -1]}]))
  ;; => 179
  )