(ns adventofcode.intcode-emulator-test
  (:require [midje.sweet :refer :all]
            [adventofcode.intcode-emulator :refer :all]))


(facts
  "about the final calculations"
  (fact
    "examples provided"
    (run-intcode [99]) => [99]
    (run-intcode [1 0 0 0 99]) => [2 0 0 0 99]
    (run-intcode [2 3 0 3 99]) => [2 3 0 6 99]
    (run-intcode [2 4 4 5 99 0]) => [2 4 4 5 99 9801]
    (run-intcode [1 1 1 4 99 5 6 0 99]) => [30 1 1 4 2 5 6 0 99]))
