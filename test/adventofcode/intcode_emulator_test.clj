(ns adventofcode.intcode-emulator-test
  (:require [midje.sweet :refer :all]
            [adventofcode.intcode-emulator :refer :all]))

(facts
  "About building the program"

  (fact
    "The build-program function, when passed a vector of integers, creates a
    program map with the instruction pointer set to 0 and the memory stored as
    a vector"
    (build-program [1 2 3 4]) => {:pointer 0 :memory [1 2 3 4]})

  (fact
    "If the build-program function is passed a second argument (which should be
    a vector of integers), that is added as the input-list to the program"
    (build-program [1 2 3 4] [5 6 7]) => {:pointer 0 :memory [1 2 3 4] :inputs [5 6 7]}))


(facts
  "about the final calculations with add and multiply operations in position mode only"

  (fact
    "When the program is passed a 99 opcode, it halts and outputs the program"
    (run (build-program [99])) => {:pointer 0 :memory [99]})

  (fact
    "Given a 1 instruction and params 0 0 0, the instruction adds pos 0 (1) and
    pos 0 (1) and puts the results in pos 0"

    (calc-new-pos-value (build-program [1 0 0 0 99])) => 2
    (do-instruction (build-program [1 0 0 0 99])) => {:pointer 4 :memory  [2 0 0 0 99]}
    (:memory (run (build-program [1 0 0 0 99]))) => [2 0 0 0 99]
    (:memory (run (build-program [1 1 1 4 99 5 6 0 99]))) => [30 1 1 4 2 5 6 0 99])

  (fact
    "Given a 2 (multiply) opcode and params 3 0 3, the instruction will multiply
    pos 3 (3) with pos 0 (2) and put the result in pos 3"

    (:memory (run (build-program [2 3 0 3 99]))) => [2 3 0 6 99])


  (facts
    "About deconstructing the instruction into it's opcode and mode parameters"

    (fact
      "When passed a full instruction, the 1 position of the return value is the
      opcode (i.e. the right-most two digits of the instruction"

      ((deconstruct-opcode-value 2) 1) => 2
      ((deconstruct-opcode-value 99) 1) => 99
      ((deconstruct-opcode-value 102) 1) => 2
      ((deconstruct-opcode-value 1102) 1) => 2
      ((deconstruct-opcode-value 11102) 1) => 2
      ((deconstruct-opcode-value 11199) 1) => 99
      ((deconstruct-opcode-value 10002) 1) => 2)

    (fact
      "The 0 position of the return value is a 3-length vector with the modes of
      each parameter, the numbers which aren't part of the opcode read right
      to left. (the 1 indicates the associated parameter will be executed in
      immediate-mode, as opposed to 0 position-mode)"

      ((deconstruct-opcode-value 2) 0) => [0 0 0]
      ((deconstruct-opcode-value 102) 0) => [1 0 0]
      ((deconstruct-opcode-value 1002) 0) => [0 1 0]
      ((deconstruct-opcode-value 1102) 0) => [1 1 0]
      ((deconstruct-opcode-value 11102) 0) => [1 1 1]
      ((deconstruct-opcode-value 10002) 0) => [0 0 1]
      ((deconstruct-opcode-value 104) 0) => [1 0 0]
      ((deconstruct-opcode-value 10102) 0) => [1 0 1])))

(facts
  "About running programs with long instructions"

  (fact
    "When a long instruction is executed for + or *, for any param which has an
    immediate-mode instruction, the value of that parameter will be taken
    directly, instead of looking up the value in the position given by the
    parameter (in the below examples the param-modes are [0 1 0] and [1 1 0]
    respectively)"

    (:memory (run (build-program [1002 4 3 5 99 0]))) => [1002 4 3 5 99 297]
    (:memory (run (build-program [1101 4 3 5 99 0]))) => [1101 4 3 5 99 7]))


(facts
  "About passing a queue of inputs to a program with Opcode 3
  (when no input list is present the program will ask the user
  for input"

  (fact
    "When given a list input with a single value, the input
    is used and put where the parameter instruct it"
    (:output (run {:pointer 0 :memory [3 5 4 5 99 -1] :inputs [10]}))
    => 10)

  (fact
    "When given a list input with multiple values, those inputs
    are used sequentially"
    (:memory (run {:pointer 0 :memory  [3 5 3 6 99 -1 -2] :inputs  [10 11]}))
    => [3 5 3 6 99 10 11]))


(facts
  "About the output opcode 4"

  (fact
    "When a 4 instruction is received in position mode, the value in the
    memory-position given by parameter one is stored in the program output
    register"
    (:output (run (build-program [4 3 99 123]))) => 123)

  (fact
    "Only one value can be stored in the output register, and it's overwritten
    if there are multiple output instructions"
    (:output (run (build-program [4 7 4 8 4 9 99 1 2 3]))) => 3)

  (fact
    "In immediate mode, the value of parameter is directly output"
    (:output (run (build-program [104 3 99 123]))) => 3
    (:output (run (build-program [104 7 104 8 104 9 99 1 2 3]))) => 9))


(facts
 "About jumping instructions"
 (def mem1 [5 9 10 99 1101 1 1 0 99 0 4])
 (def mem2 [5 9 10 99 1101 1 1 0 99 1 4])

 (fact
   "When a jump-if-true instruction (opcode 5) is received, if the value in the
   memory position given by param1 is 0, nothing happens and the instruction pointer
   increments by 3"
   (:pointer (do-instruction {:pointer 0 :memory mem1})) => 3
   (:memory (run (build-program mem1))) => [5 9 10 99 1101 1 1 0 99 0 4])

 (fact
   "But if the value of the memory-postion given by param1 is non-zero, the
   instruction-pointer jumps to the value given by the memory-position in param2"
   (:pointer (do-instruction {:pointer 0 :memory mem2})) => 4
   (:memory (run (build-program mem2))) => [2 9 10 99 1101 1 1 0 99 1 4])

 (fact
  "Opcode 6, jumps-if-false, does the opposite"

  (def mem3 [6 9 10 99 1101 1 1 0 99 1 4])
  (def mem4 [6 9 10 99 1101 1 1 0 99 0 4])

  (:pointer (do-instruction {:pointer 0 :memory mem3})) => 3
  (:memory (run (build-program mem3))) => mem3

  (:pointer (do-instruction {:pointer 0 :memory mem4})) => 4
  (:memory (run (build-program mem4))) => [2 9 10 99 1101 1 1 0 99 0 4])

 (fact
   "Jump-if-true in Immediate mode works as you'd expect"
   (:memory (run (build-program [105 0 9 99 1101 1 1 0 99 4]))) => [105 0 9 99 1101 1 1 0 99 4]
   (:memory (run (build-program [105 1 9 99 1101 1 1 0 99 4]))) => [2 1 9 99 1101 1 1 0 99 4]
   (:memory (run (build-program [1105 0 5 99 1101 1 1 0 99]))) => [1105 0 5 99 1101 1 1 0 99]
   (:memory (run (build-program [1105 1 4 99 1101 1 1 0 99]))) => [2 1 4 99 1101 1 1 0 99])

 (fact
   "As does jump-if-false"
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
  "About the amplifier control software"
  (fact
    "When passed the software, an initial input and the phase order, the program
    calculates the output"
    (amplifier-controller [3,15,3,16,1002,16,10,16,1,16,15,15,4,15,99,0,0] 0 [4 3 2 1 0]) => 43210))