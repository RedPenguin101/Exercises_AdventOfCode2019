(ns adventofcode.manhattan-calc-test
  (:require [midje.sweet :refer :all]
            [adventofcode.manhattan-calc :refer :all]))


(facts
  "about coordinate calculator"
  (fact
    "any coord with an instruction length of zero should not change"
    (calc-next-coord [1 1] "U0") => [1 1])
  (fact
    "a cood with one up or down should change the y coord by one"
    (calc-next-coord [1 1] "U1") => [1 2]
    (calc-next-coord [1 1] "D1") => [1 0])
  (fact
    "a cood with one left or right should change the y coord by one"
    (calc-next-coord [1 1] "R1") => [2 1]
    (calc-next-coord [1 1] "L1") => [0 1])
  (fact
    "a path should be updated with the next instrcution"
    (update-path [[1 1] [1 2]] "U1") => [[1 1] [1 2] [1 3]])
  )