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
    (build-path "U1,R1,D1") => '([0 1] [1 1] [1 0])))

(facts
  "about min distance calcing"
  (fact
    (min-man-distance
      (find-intersections
        (build-path "R75,D30,R83,U83,L12,D49,R71,U7,L72")
        (build-path "U62,R66,U55,R34,D71,R55,D58,R83")))
    => 159))

(facts
  "about path-distance-to calc"
  (fact (path-distance-to [[0 1] [0 2] [0 3] [0 4] [0 5]] [0 5]) => 5))

(facts
  "about min distance to"
  (fact (min-path-distance
          (build-path "R75,D30,R83,U83,L12,D49,R71,U7,L72")
          (build-path "U62,R66,U55,R34,D71,R55,D58,R83"))
        => 610)
  (fact (min-path-distance
          (build-path "R98,U47,R26,D63,R33,U87,L62,D20,R33,U53,R51")
          (build-path "U98,R91,D20,R16,D67,R40,U7,R15,U6,R7"))
        => 410)
  )