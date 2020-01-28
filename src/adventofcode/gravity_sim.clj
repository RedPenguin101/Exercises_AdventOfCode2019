(ns adventofcode.gravity-sim)

(comment
  "A body is represented as a map of position vector and velocity vector"
  {:position [0 0 0] :velocity [1 1 1]})

(defn- update-position [{:keys [position velocity] :as body}]
  (assoc body :position (vec (map #(+ %1 %2) position velocity))))

(defn- vel-modifier
  "given two positions on an axis, return a function to apply to the velocity
   of the asteroid at position a at the subsequent step"
  [a b]
  (cond
    (zero? (- a b)) identity
    (pos? (- a b))  dec
    (neg? (- a b))  inc))

(defn- update-velocity [body1 body2]
  (assoc 
   body1 
   :velocity 
   (vec (map #(%1 %2) 
             (map #(vel-modifier %1 %2) 
                  (:position body1) 
                  (:position body2)) 
             (:velocity body1)))))

(defn- update-velocities [bodies]
  (doall ;nessessary to prevent stackoverflow
   (for [body bodies]
     (reduce update-velocity body bodies))))

(defn step [bodies]
  (->> (update-velocities bodies)
       (map update-position)))

(defn steps [bodies times]
  (if (= times 0)
    bodies
    (recur (step bodies) (dec times)))) 

(defn- sum-of-abs [vector]
  (apply + (map #(Math/abs %) vector)))

(defn total-energy [{:keys [position velocity]}]
  (* (sum-of-abs position) 
     (sum-of-abs velocity)))

(comment 
  (reduce + (map total-energy [{:position [2 1 -3], :velocity [-3 -2 1]}
                               {:position [1 -8 0], :velocity [-1 1 3]}
                               {:position [3 -6 1], :velocity [3 2 -3]}
                               {:position [2 0 4], :velocity [1 -1 -1]}]))
  ;; => 179
  )


;;;;;;;;;;;;;;;;;;;;;;;;
;; getting input
;;;;;;;;;;;;;;;;;;;;;;;;

(def input-pattern #"<x=(-?\d+), y=(-?\d+), z=(-?\d+)>")

(defn- parse-input-coord [string]
  {:position (vec (map #(Integer/parseInt %) (rest (re-matches input-pattern string))))
   :velocity [0 0 0]})

(defn slurp-to-bodies [filename]
  (map parse-input-coord (clojure.string/split-lines (slurp filename))))

(comment
  "day 12 part one"
  (def start (slurp-to-bodies "resources/inputday12.txt"))
  (->> (steps start 1000)
       (map total-energy)
       (apply +))
  ;; => 13045
  )


;;;;;;;;;;;;;;;;;;;;;;;;
;; acting on a single axis
;;;;;;;;;;;;;;;;;;;;;;;;

(comment 
  "an alternative representation is as positions and velocities
  on a single axis"
  
  (def x {:positions [6 -9 9 4] :velocities [0 0 0 0] :axis :x})
  
  "the modifier to the velocity of an asteroid is the number of asteroids
  with a greater position value than it, less the number with a lesser"
  
  "take the position 6, filter-count the number of entries > 6 (1)
  and the number of entries < 6 (2) and deduct the former from the latter
  and you have the velocity change")

(defn- velocity-mod* [pos pos-vect]
  (- (count (filter #(< pos %) pos-vect)) (count (filter #(> pos %) pos-vect))))

(defn- velocity-mod [positions]
  (map #(velocity-mod* % positions) positions))

(velocity-mod [6 -9 9 4])

(defn axis-step [{:keys [positions velocities] :as bodies}]
  (let [new-vels (map + velocities (velocity-mod positions))]
    (assoc bodies 
           :velocities new-vels
           :positions (map + positions new-vels))))

(comment "from tests"
  (axis-step {:positions [-1 2 4 3] :velocities [0 0 0 0]})
  ;; => {:positions (2 3 1 2), :velocities (3 1 -3 -1)}
  )