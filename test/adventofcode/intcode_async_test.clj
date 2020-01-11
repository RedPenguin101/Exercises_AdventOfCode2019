(ns adventofcode.intcode-async-test
  (:require [adventofcode.intcode-async :refer :all]
            [midje.sweet :refer :all]))

(fact "add and mult from day 2"
  (:memory (simple-run [1,0,0,0,99])) => [2 0 0 0 99]
  (:memory (simple-run [2,3,0,3,99])) => [2 3 0 6 99]
  (:memory (simple-run [2 4 4 5 99 0])) => [2 4 4 5 99 9801]
  (:memory (simple-run [1 1 1 4 99 5 6 0 99])) => [30 1 1 4 2 5 6 0 99])

(fact "input and outputs from day 5"
  (collect-output [4 5 4 6 99 123 321] nil) => [123 321]
  (collect-output [3 0 4 7 4 8 99 123 321] 1) => [123 321]
  )


(fact "immediate mode for multiply and add"
  (:memory (simple-run [1002 4 3 4 33])) => [1002 4 3 4 99]
  (:memory (simple-run [1001 4 3 4 96])) => [1001 4 3 4 99]
  )

(fact "lt / eq tests from day 5"
  (collect-output [3 9 8 9 10 9 4 9 99 -1 8] 5) => [0]
  (collect-output [3 9 8 9 10 9 4 9 99 -1 8] 8) => [1]

  (collect-output [3 9 7 9 10 9 4 9 99 -1 8] 5) => [1]
  (collect-output [3 9 7 9 10 9 4 9 99 -1 8] 8) => [0]

  (collect-output [3 3 1108 -1 8 3 4 3 99] 5) => [0]
  (collect-output [3 3 1108 -1 8 3 4 3 99] 8) => [1]

  (collect-output [3 3 1107 -1 8 3 4 3 99] 5) => [1]
  (collect-output [3 3 1107 -1 8 3 4 3 99] 8) => [0]) 

(fact "jump-if-true test in immediate mode"
  (collect-output [1105 1 4 99 4 0 99]) => [1105]
  (collect-output [1105 0 4 99 4 0 99]) => [])

(fact "jump-if-true in position mode"
  (collect-output [5 4 4 99 4 0 99]) => [5]
  (collect-output [5 5 4 99 4 0 99]) => [])
  
(fact "jump-if-false test in immediate mode"
  (collect-output [1106 1 4 99 4 0 99]) => []
  (collect-output [1106 0 4 99 4 0 99]) => [1106])

(fact "jump-if-fale in position mode"
  (collect-output [6 4 4 99 4 0 99]) => []
  (collect-output [6 5 4 99 4 0 99]) => [6])
  
(def adder [3 1 1 1 2 1 4 1 99])
; a program that adds 1 to its input

(fact "running a single computer in async mode"
  (collect-output adder 5) => [6]
  (collect-output-async adder 5) => [6])

(fact "all the same tests as above, but in async mode
      NOTE you have to provide an input argument to async, even if it's nil"
  (collect-output-async [4 5 4 6 99 123 321] nil) => [123 321]
  (collect-output-async [3 0 4 7 4 8 99 123 321] 1) => [123 321]
  (collect-output-async [3 9 8 9 10 9 4 9 99 -1 8] 5) => [0]
  (collect-output-async [3 9 8 9 10 9 4 9 99 -1 8] 8) => [1]
  (collect-output-async [3 9 7 9 10 9 4 9 99 -1 8] 5) => [1]
  (collect-output-async [3 9 7 9 10 9 4 9 99 -1 8] 8) => [0]
  (collect-output-async [3 3 1108 -1 8 3 4 3 99] 5) => [0]
  (collect-output-async [3 3 1108 -1 8 3 4 3 99] 8) => [1]
  (collect-output-async [3 3 1107 -1 8 3 4 3 99] 5) => [1]
  (collect-output-async [3 3 1107 -1 8 3 4 3 99] 8) => [0]
  (collect-output-async [1105 1 4 99 4 0 99] nil) => [1105]
  (collect-output-async [1105 0 4 99 4 0 99] nil) => []
  (collect-output-async [5 4 4 99 4 0 99] nil) => [5]
  (collect-output-async [5 5 4 99 4 0 99] nil) => []
  (collect-output-async [1106 1 4 99 4 0 99] nil) => []
  (collect-output-async [1106 0 4 99 4 0 99] nil) => [1106]
  (collect-output-async [6 4 4 99 4 0 99] nil) => []
  (collect-output-async [6 5 4 99 4 0 99] nil) => [6])


(fact
 "async non-looping amplifier chains"
 (async-amps [3 0 3 2 1 0 2 0 4 0 99] [1 2 3 4 5] 6) => 21
 
 (async-amps 
  [3,15,3,16,1002,16,10,16,1,16,15,15,4,15,99,0,0]
  [4 3 2 1 0]
  0) => 43210
 
 (async-amps
  [3,23,3,24,1002,24,10,24,1002,23,-1,23,101,5,23,23,1,24,23,23,4,23,99,0,0]
  [0,1,2,3,4]
  0) => 54321
 
 (async-amps
  [3,31,3,32,1002,32,10,32,1001,31,-2,31,1007,31,0,33,
   1002,33,7,33,1,33,31,31,1,32,31,31,4,31,99,0,0,0]
  [1,0,4,3,2]
  0) => 65210)

(facts
 "async-looping amp chains"
 (amps-looped
  [3,26,1001,26,-4,26,3,27,1002,27,2,27,1,27,26,27,4,27,1001,28,-1,28,1005,28,6,99,0,0,5]
  [9,8,7,6,5]
  0) => 139629729

 (amps-looped
  [3,52,1001,52,-5,52,3,53,1,52,56,54,1007,54,5,55,1005,55,26,1001,54
   -5,54,1105,1,12,1,53,54,53,1008,54,0,55,1001,55,1,55,2,53,55,53,4
   53,1001,56,-1,56,1005,56,6,99,0,0,0,0,10]
  [9,7,8,5,6]
  0) => 18216
 )


(comment "new parameter mode: 2, relative. Selects a position relative to cursor
          refers to itself + relative base RB. When RB=0, RMode is same as position
          If RB is 50, Rmode param of -7 will look to position 43.
          Opcode 9 is RB adjust. It has 1 param: the RB changes by the amount of the param
          opcode 9 can be in any mode (assumption).")


(comment "the computer should model available memory - i.e. if the initial program is
          10 integers long, and the program tries to write to memory address 15, it should 
          be allowed to do that. Memory is intialised at zero.
          Do this dynamically or with brute force?")

(fact
 (:rel-base (simple-run [109 19 99])) => 19
 (:rel-base (simple-run [109 19 109 -5 99])) => 14
 (:rel-base (simple-run [9 0 99])) => 9
 )