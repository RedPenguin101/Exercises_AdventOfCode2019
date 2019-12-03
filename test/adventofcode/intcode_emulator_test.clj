(ns adventofcode.intcode-emulator-test
  (:require [midje.sweet :refer :all]
            [adventofcode.intcode-emulator :refer :all]))


(facts
  "about the final calculations"
  (fact
    "examples provided"
    (run [99]) => [99]
    (run [1 0 0 0 99]) => [2 0 0 0 99]
    (run [2 3 0 3 99]) => [2 3 0 6 99]
    (run [2 4 4 5 99 0]) => [2 4 4 5 99 9801]
    (run [1 1 1 4 99 5 6 0 99]) => [30 1 1 4 2 5 6 0 99]))

(facts
  "about loading int-code data"
  (fact
    "intcode test load"
    (load-memory-state "intcode_test.txt") => [0 1 2 3 4]))
