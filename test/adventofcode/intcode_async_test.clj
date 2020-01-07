(ns adventofcode.intcode-async-test
  (:require [adventofcode.intcode-async :refer :all]
            [midje.sweet :refer :all]))

(fact "add and mult from day 2"
  (:memory (simple-run [1,0,0,0,99])) => [2 0 0 0 99]
  (:memory (simple-run [2,3,0,3,99])) => [2 3 0 6 99]
  (:memory (simple-run [2 4 4 5 99 0])) => [2 4 4 5 99 9801]
  (:memory (simple-run [1 1 1 4 99 5 6 0 99])) => [30 1 1 4 2 5 6 0 99])

(fact "input and outputs from day 5"
  (collect-output [4 5 4 6 99 123 321] nil) => [123 321])

(fact "long opcode desconstruction"
  (deconstruct-opcode-value 1002) => [[0 1 0] 2])

(fact "immediate"
  (immediate? {:memory [1002] :pointer 0} 0) => false
  (immediate? {:memory [1002] :pointer 0} 1) => true
  (immediate? {:memory [1002] :pointer 0} 2) => false)

(fact "calc new position value in immediate mode"
  (calc-new-pos-value {:memory [1002 4 3 4 33] :pointer 0}) => 99)

(fact "immediate mode for multiply and add"
  (:memory (simple-run [1002 4 3 4 33])) => [1002 4 3 4 99]
  (:memory (simple-run [1001 4 3 4 96])) => [1001 4 3 4 99]
  )

