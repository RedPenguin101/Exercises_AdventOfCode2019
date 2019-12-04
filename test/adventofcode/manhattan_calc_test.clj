(ns adventofcode.manhattan-calc-test
  (:require [midje.sweet :refer :all]
            [adventofcode.manhattan-calc :refer :all]))

(facts
  "about moving"
  (fact ""
        (move [0 0] "U5") => '([0 1] [0 2] [0 3] [0 4] [0 5])))

(facts
  "about path building"
  (fact
    (build-path "U1,R1,D1") => '([0 0] [0 1] [1 1] [1 0])))

(facts
  "about intersections"
  (fact
    (find-intersections
      (build-path "R75,D30,R83,U83,L12,D49,R71,U7,L72")
      (build-path "U62,R66,U55,R34,D71,R55,D58,R83"))
    => false))