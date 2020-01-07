(ns adventofcode.intcode-async-test
  (:require [adventofcode.intcode-async :refer :all]
            [midje.sweet :refer :all]))

(fact "test add and mult from day 2"
  (:memory (simple-run [1,0,0,0,99])) => [2 0 0 0 99]
  (:memory (simple-run [2,3,0,3,99])) => [2 3 0 6 99]
  (:memory (simple-run [2 4 4 5 99 0])) => [2 4 4 5 99 9801]
  (:memory (simple-run [1 1 1 4 99 5 6 0 99])) => [30 1 1 4 2 5 6 0 99])

(fact "input and outputs from day 5"
  )

