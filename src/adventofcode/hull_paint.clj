(ns adventofcode.hull-paint
  (:require [adventofcode.intcode-async :refer [run-async]]))

(comment
  "a surface is a vector of vectors which represent the path of the
  robot over the surface. Each vector consists of an x-coord, y-coord and color
  The vector is ordered, so in the case the robot has visitied the point twice,
  later one will be the return value")

(defn- lookup-color [surface [x y :as point]]
  (if ((set (map drop-last surface)) point)
    ((last (filter #(and (= x (% 0)) (= y (% 1))) surface)) 2)
    0))

(comment
  (lookup-color [[1 1 0] [1 2 1] [1 1 1]] [1 1])
  ;; => 1
  "returns 0 (black) if the co-ordinate isn't there"
  (lookup-color [[1 1 0] [1 2 1] [1 1 1]] [2 1])
  ;; => 0
  )

(defn- new-coord [[x y] curr-dir instr]
  (case (mod (+ curr-dir (if (zero? instr) -1 1)) 4)
    0 [(inc x) y]
    1 [x (inc y)]
    2 [(dec x) y]
    3 [x (dec y)]))
