(ns adventofcode.manhattan-calc-test
  (:require [midje.sweet :refer :all]
            [adventofcode.manhattan-calc :refer :all]))


(facts
  "about next coordinate calculator"
  (fact
    "any coord with an instruction length of zero should not change"
    (calc-end [1 1] "U0") => [1 1])
  (fact
    "a cood with one up or down should change the y coord by one"
    (calc-end [1 1] "U1") => [1 2]
    (calc-end [1 1] "D1") => [1 0])
  (fact
    "a cood with one left or right should change the y coord by one"
    (calc-end [1 1] "R1") => [2 1]
    (calc-end [1 1] "L1") => [0 1]))

(facts
  "about creating lines"
  (fact "a line creates correctly"
        (create-line [5 5] "U5") => {:start [5 5] :end [5 10]}))

(facts
  "about creating paths"
  (fact "a path creates from a single instruction"
        (build-path ["U5"]) => [{:start [0 0] :end [0 5]}]))

(facts
  "about the minimum manhattan distance"
  (fact "from the problem"
        (min-manhatten-intersection
          (build-path (str->instrs "R75,D30,R83,U83,L12,D49,R71,U7,L72"))
          (build-path (str->instrs "U62,R66,U55,R34,D71,R55,D58,R83"))) => 159))