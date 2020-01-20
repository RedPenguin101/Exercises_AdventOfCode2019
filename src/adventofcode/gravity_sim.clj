(ns adventofcode.gravity-sim)

(comment
  "A body is represented as a map of position vector and velocity vector"
  {:position [0 0 0] :velocity [1 1 1]})

(defn update-position [{:keys [position velocity] :as body}]
  (assoc body :position (map #(+ %1 %2) position velocity)))


