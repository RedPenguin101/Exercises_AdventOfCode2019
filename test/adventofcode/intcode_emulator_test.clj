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

(facts
  "about desconstructing the opcode"
  (fact
    "the opcode is desconstructed into an opcode"
    ((deconstruct-opcode-value 2) 1) => 2
    ((deconstruct-opcode-value 102) 1) => 2
    ((deconstruct-opcode-value 1102) 1) => 2
    ((deconstruct-opcode-value 11102) 1) => 2
    ((deconstruct-opcode-value 11199) 1) => 99
    ((deconstruct-opcode-value 10002) 1) => 2)
  (fact
    "the parameters deconstruct into 3 parameters"
    ((deconstruct-opcode-value 2) 0) => [0 0 0]
    ((deconstruct-opcode-value 102) 0) => [1 0 0]
    ((deconstruct-opcode-value 1002) 0) => [0 1 0]
    ((deconstruct-opcode-value 1102) 0) => [1 1 0]
    ((deconstruct-opcode-value 11102) 0) => [1 1 1]
    ((deconstruct-opcode-value 10002) 0) => [0 0 1]
    ((deconstruct-opcode-value 10102) 0) => [1 0 1]))

(facts
  "about running programs with long opcodes"
  (future-fact "long opcodes with immediate mode and add / mult only"
        (run [1002 4 3 5 99 0]) => [1002 4 3 5 99 297]))