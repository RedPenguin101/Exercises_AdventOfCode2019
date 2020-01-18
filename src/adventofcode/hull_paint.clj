(ns adventofcode.hull-paint
  (:require [adventofcode.intcode-async :refer [run-async]]
            [clojure.core.async :refer [chan <!! >!!]]))

(comment
  "a surface is a vector of vectors which represent the path of the
  robot over the surface. Each vector consists of an x-coord, y-coord and color
  The vector is ordered, so in the case the robot has visitied the point twice,
  later one will be the return value")

(defn- new-dir [curr-dir turn]
  (mod (+ curr-dir (if (zero? turn) -1 1)) 4))

(defn- new-coord [[x y] curr-dir turn]
  (case (new-dir curr-dir turn)
    0 [(inc x) y]
    1 [x (inc y)]
    2 [(dec x) y]
    3 [x (dec y)]))

(defn- update-surface [surface [x y] color]
  (conj surface [x y color]))

(defn run-robot [surface dir in out]
  (let [color (<!! out)
        turn (<!! out)
        new-surface (update-surface surface (new-coord ((last surface) 2) dir turn) color)]
    (if color
      (do (>!! in ((last new-surface) 2))
          (recur new-surface (new-dir dir turn) in out))
      surface)))

(comment 
  (def program 
    (vec (map #(bigint %)
              (->  "resources/inputday11.txt"
                   slurp
                   clojure.string/trim
                   (clojure.string/split #",")))))

  (let [in (chan)
        [out] (run-async program in)]
    (run-robot [0 0 0] 0 in out))
  )