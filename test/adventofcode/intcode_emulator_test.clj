(ns adventofcode.intcode-emulator-test
  (:require [midje.sweet :refer :all]
            [adventofcode.intcode-emulator :refer :all]))

(facts
  "about loading int-code data"
  (fact
    "intcode test load"
    (load-memory-state "intcode_test.txt") => [0 1 2 3 4]))

(facts
  "about building the program"
  (build-program [1 2 3 4]) => {:pointer 0 :memory [1 2 3 4]})

(facts
  "about the final calculations with add and multiply operations
  in position mode only"

  (fact "trivial case" (:memory (run (build-program [99]))) => [99]))

(facts
  "about doing + and * instructions (opcodes 1 and 2)"

  (fact
    "given a 1 instruction and params 0 0 0, the instruction adds
    pos 0 (1) and pos 0 (1) and puts the results in pos 0"

    (calc-new-pos-value (build-program [1 0 0 0 99])) => 2
    (do-instruction (build-program [1 0 0 0 99])) => {:pointer 4
                                                      :memory [2 0 0 0 99]}
    (:memory (run (build-program [1 0 0 0 99]))) => [2 0 0 0 99])

  (fact
    "given a 2 (multiply) opcode and params 3 0 3, the instruction
    will multiply pos 3 (3) with pos 0 (2) and put the result in
    pos 3"
    (:memory (run (build-program [2 3 0 3 99]))) => [2 3 0 6 99])

  (:memory (run (build-program [1 1 1 4 99 5 6 0 99]))) => [30 1 1 4 2 5 6 0 99])


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
        (:memory (run (build-program [1002 4 3 5 99 0]))) => [1002 4 3 5 99 297]
        (:memory (run (build-program [1101 4 3 5 99 0]))) => [1101 4 3 5 99 7]))

(facts
  "about printing output (opcode4)"
  (fact
    "without any position modes"
    (:output (run (build-program [4 3 99 123]))) => 123
    (:output (run (build-program [4 7 4 8 4 9 99 1 2 3]))) => 3)
  (fact
    "with posmode"
    (:output (run (build-program [104 3 99 123]))) => 3
    (:output (run (build-program [104 7 104 8 104 9 99 1 2 3]))) => 9))


(facts
 "about conditionals in position mode"
 (fact
  "opcode 5 jumps-if-true, does nothing if false"
  (:memory (run (build-program [5 9 10 99 1101 1 1 0 99 0 4]))) => [5 9 10 99 1101 1 1 0 99 0 4]
  (:memory (run (build-program [5 9 10 99 1101 1 1 0 99 1 4]))) => [2 9 10 99 1101 1 1 0 99 1 4])
 (fact
  "opcode 6 jumps-if-false, does nothing if true"
  (:memory (run (build-program [6 9 10 99 1101 1 1 0 99 1 4]))) => [6 9 10 99 1101 1 1 0 99 1 4]
  (:memory (run (build-program [6 9 10 99 1101 1 1 0 99 0 4]))) => [2 9 10 99 1101 1 1 0 99 0 4]))


(facts
 "about conditionals in immediate mode"
 (fact
  "opcode 5 jumps-if-true, does nothing if false"
  (:memory (run (build-program [105 0 9 99 1101 1 1 0 99 4]))) => [105 0 9 99 1101 1 1 0 99 4]
  (:memory (run (build-program [105 1 9 99 1101 1 1 0 99 4]))) => [2 1 9 99 1101 1 1 0 99 4]
  (:memory (run (build-program [1105 0 5 99 1101 1 1 0 99]))) => [1105 0 5 99 1101 1 1 0 99]
  (:memory (run (build-program [1105 1 4 99 1101 1 1 0 99]))) => [2 1 4 99 1101 1 1 0 99])
 (fact
  "opcode 6 jumps-if-false, does nothing if true"
  (:memory (run (build-program [106 1 9 99 1101 1 1 0 99 4]))) => [106 1 9 99 1101 1 1 0 99 4]
  (:memory (run (build-program [106 0 9 99 1101 1 1 0 99 4]))) => [2 0 9 99 1101 1 1 0 99 4]
  (:memory (run (build-program [1106 1 4 99 1101 1 1 0 99]))) => [1106 1 4 99 1101 1 1 0 99]
  (:memory (run (build-program [1106 0 4 99 1101 1 1 0 99]))) => [2 0 4 99 1101 1 1 0 99]))

(facts
 "about less than and equal to opcodes in immediate mode"
 (fact
  "opcode 7 (less than): if 1st param is LT 2nd, stores 1 in pos given by param 3"
  (:memory (run (build-program [1107 2 1 0 99]))) => [0 2 1 0 99]
  (:memory (run (build-program [1107 1 2 0 99]))) => [1 1 2 0 99]
 (fact
  "opcode 8 (equal to): if 1st param is = 2nd, stores 1 in pos given by param 3"
  (:memory (run (build-program [1108 2 1 0 99]))) => [0 2 1 0 99]
  (:memory (run (build-program [1108 1 2 0 99]))) => [0 1 2 0 99]
  (:memory (run (build-program [1108 1 1 0 99]))) => [1 1 1 0 99])))

(facts
 "about less than and equal to opcodes in position mode"
 (fact
  "opcode 7 (less than): if 1st param is LT 2nd, stores 1 in pos given by param 3"
  (:memory (run (build-program [7 5 6 0 99 2 1]))) => [0 5 6 0 99 2 1]
  (:memory (run (build-program [7 5 6 0 99 1 2]))) => [1 5 6 0 99 1 2]
  (:memory (run (build-program [7 5 6 0 99 1 1]))) => [0 5 6 0 99 1 1])
 (fact
  "opcode 8 (equal to): if 1st param is = 2nd, stores 1 in pos given by param 3"
  (:memory (run (build-program [8 5 6 0 99 2 1]))) => [0 5 6 0 99 2 1]
  (:memory (run (build-program [8 5 6 0 99 1 2]))) => [0 5 6 0 99 1 2]
  (:memory (run (build-program [8 5 6 0 99 1 1]))) => [1 5 6 0 99 1 1]))


(facts
  "About accepting phases"
(fact
    "add phase values"
    (add-phases (build-program [1 0 0 0 99]) [0 1 2 3 4]) => {:pointer 0 :memory [1 0 0 0 99] :current-phase 0 :phases [0 1 2 3 4]})

  (fact
    "an input instruction, when given a program with phase values
    substitutes the phase value for the user input"
    (do-instruction
      (add-phases (build-program [3 3 99 -1]) [0 1 2 3 4]))
    => {:pointer 2 :memory [3 3 99 0] :current-phase 0 :phases [0 1 2 3 4]})

  (fact
    "about running with phases"
    (println "expect 43210")
    (run-with-phases
      [3,15,3,16,1002,16,10,16,1,16,15,15,4,15,99,0,0]
      [4 3 2 1 0])))