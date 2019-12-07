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
    ((deconstruct-opcode-value 99) 1) => 99
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
    ((deconstruct-opcode-value 104) 0) => [1 0 0]
    ((deconstruct-opcode-value 10102) 0) => [1 0 1]))

(facts
  "about running programs with long opcodes"
  (fact "long opcodes with different modes and add / mult only"
        (run [1002 4 3 5 99 0]) => [1002 4 3 5 99 297]
        (run [1101 4 3 5 99 0]) => [1101 4 3 5 99 7]))

(facts
  "about printing output (opcode4)"
  (fact
    "without any position modes"
    (println "expect 123")
    (run [4 3 99 123]) => [4 3 99 123]
    (println "expect 1 2 3")
    (run [4 7 4 8 4 9 99 1 2 3]) => [4 7 4 8 4 9 99 1 2 3])
  (fact
    "with posmode"
    (println "expect 3")
    (run [104 3 99 123]) => [104 3 99 123]
    (println "expect 7 8 9")
    (run [104 7 104 8 104 9 99 1 2 3]) => [104 7 104 8 104 9 99 1 2 3])
        )

(facts
 "about conditionals in position mode"
 (fact
  "opcode 5 jumps-if-true, does nothing if false"
  (run [5 9 10 99 1101 1 1 0 99 0 4]) => [5 9 10 99 1101 1 1 0 99 0 4]
  (run [5 9 10 99 1101 1 1 0 99 1 4]) => [2 9 10 99 1101 1 1 0 99 1 4])
 (fact
  "opcode 6 jumps-if-false, does nothing if true"
  (run [6 9 10 99 1101 1 1 0 99 1 4]) => [6 9 10 99 1101 1 1 0 99 1 4]
  (run [6 9 10 99 1101 1 1 0 99 0 4]) => [2 9 10 99 1101 1 1 0 99 0 4]))


(facts
 "about conditionals in immediate mode"
 (fact
  "opcode 5 jumps-if-true, does nothing if false"
  (run [105 0 9 99 1101 1 1 0 99 4]) => [105 0 9 99 1101 1 1 0 99 4]
  (run [105 1 9 99 1101 1 1 0 99 4]) => [2 1 9 99 1101 1 1 0 99 4]
  (run [1105 0 5 99 1101 1 1 0 99]) => [1105 0 5 99 1101 1 1 0 99]
  (run [1105 1 4 99 1101 1 1 0 99]) => [2 1 4 99 1101 1 1 0 99])
 (fact
  "opcode 6 jumps-if-false, does nothing if true"
  (run [106 1 9 99 1101 1 1 0 99 4]) => [106 1 9 99 1101 1 1 0 99 4]
  (run [106 0 9 99 1101 1 1 0 99 4]) => [2 0 9 99 1101 1 1 0 99 4]
  (run [1106 1 4 99 1101 1 1 0 99]) => [1106 1 4 99 1101 1 1 0 99]
  (run [1106 0 4 99 1101 1 1 0 99]) => [2 0 4 99 1101 1 1 0 99]))

(facts
 "about less than and equal to opcodes in immediate mode"
 (fact
  "opcode 7 (less than): if 1st param is LT 2nd, stores 1 in pos given by param 3"
  (run [1107 2 1 0 99]) => [0 2 1 0 99]
  (run [1107 1 2 0 99]) => [1 1 2 0 99]
 (fact
  "opcode 8 (equal to): if 1st param is = 2nd, stores 1 in pos given by param 3"
  (run [1108 2 1 0 99]) => [0 2 1 0 99]
  (run [1108 1 2 0 99]) => [0 1 2 0 99]
  (run [1108 1 1 0 99]) => [1 1 1 0 99])))

(facts
 "about less than and equal to opcodes in position mode"
 (fact
  "opcode 7 (less than): if 1st param is LT 2nd, stores 1 in pos given by param 3"
  (run [7 5 6 0 99 2 1]) => [0 5 6 0 99 2 1]
  (run [7 5 6 0 99 1 2]) => [1 5 6 0 99 1 2]
  (run [7 5 6 0 99 1 1]) => [0 5 6 0 99 1 1])
 (fact
  "opcode 8 (equal to): if 1st param is = 2nd, stores 1 in pos given by param 3"
  (run [8 5 6 0 99 2 1]) => [0 5 6 0 99 2 1]
  (run [8 5 6 0 99 1 2]) => [0 5 6 0 99 1 2]
  (run [8 5 6 0 99 1 1]) => [1 5 6 0 99 1 1]))