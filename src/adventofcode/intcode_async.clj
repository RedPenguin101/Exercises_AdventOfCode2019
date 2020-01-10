(ns adventofcode.intcode-async
  (:require [clojure.math.combinatorics :as combo]
            [clojure.core.async :as a
                                :refer [>!! >! <!! <! chan close!]]))


(defn- boot [memory]
  {:memory memory :pointer 0})


(defn- params-vec-from [param-vals]
  (reverse (map #(Character/digit % 10) (format "%03d" param-vals))))


(defn- get-instruction-params [pointer]
  (vec (range (+ 1 pointer) (+ 4 pointer))))


(defn- deconstruct-opcode-value 
  "Given a long opcode of up to 5 digits, returns a vector of the param-values
   (a vector of the first three digits) and the opcode (the last two digits)"
  [number]
  (let [param-vals (quot number 100)]
    [(params-vec-from param-vals) (- number (* param-vals 100))]))


(defn- opcode [number]
  ((deconstruct-opcode-value number) 1))


(defn- immediate? [{:keys [memory pointer]} position]
  (= 1 (nth ((deconstruct-opcode-value (memory pointer)) 0) position)))


(defn- bool-to-int [input]
  (case input
    true 1
    false 0
    input))


(defn- calc-new-pos-value [{:keys [memory pointer] :as state}]
  (bool-to-int 
    (({1 + 2 * 7 < 8 =} (opcode (memory pointer))) 
     ((if (immediate? state 0) identity memory) (memory (+ pointer 1))) 
     ((if (immediate? state 1) identity memory) (memory (+ pointer 2))))))


(comment "below gives example of how calc-new-pos determines immediate vs
         position mode and applies the appropriate function"

  (def s {:memory [1002 4 3 4 33] :pointer 0})
  (def m (:memory s))
  (def p (:pointer s))

  (immediate? s 0) ;=> false, arg 1 is pos mode
  (immediate? s 1) ;=> true, arg 2 is immediate mode
  
  ((if (immediate? s 0) identity m) (m (+ p 1))) ;=> 33, because in pos mode
  ((if (immediate? s 1) identity m) (m (+ p 2))) ;=> 3 in immediate mode
  (calc-new-pos-value s) ;=> 99, or 33 * 3
  )

(comment "calc new position value deals with lt and eq like this"
  (calc-new-pos-value {:memory [1107 1 2 3 -1] :pointer 0})
  "returns 1 (true) because 1 is less than 2"
  (calc-new-pos-value {:memory [1107 2 1 3 -1] :pointer 0})
  "returns 0 (false) because 1 is not less than 1"
  (calc-new-pos-value {:memory [1108 2 1 3 -1] :pointer 0})
  (calc-new-pos-value {:memory [1108 2 2 3 -1] :pointer 0})
  "equals mode: these return 0 and 1 respectively.")


(defn- do-operation [{:keys [pointer, memory] :as state}]
  ;(println "op" state)
  (-> state
      (assoc :pointer (+ 4 pointer))
      (assoc-in [:memory (memory (+ 3 pointer))] 
                (calc-new-pos-value state))))


(defn- jump-if [{:keys [pointer, memory] :as state}]
  (let [[param-modes opcode] (deconstruct-opcode-value (memory pointer))
        immediate_modes (map #(= 1 %) param-modes)
        args (map memory (get-instruction-params pointer))
        [x jump-to-pos] (map #(if %1 %2 (memory %2)) immediate_modes args)
        jump? (= (= 5 opcode) (not= 0 x))]
    (assoc state :pointer (if jump? jump-to-pos (+ 3 pointer)))))


(defn- process-input [{:keys [pointer, memory] :as state} input]
  ;(println "in" state "input" input)
  (-> state
      (assoc :pointer (+ 2 pointer))
      (assoc-in [:memory (memory (+ 1 pointer))] input)))


(defn- process-output [{:keys [pointer, memory] :as state}]
  ;(println "out" state)
  (memory (memory (+ 1 pointer))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; run intcode computer single threaded
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn- run-singly [{:keys [pointer memory] :as state} inputs outputs]
  (cond
    (= 99 (opcode (memory pointer))) [outputs state]

    (= 3 (opcode (memory pointer))) 
      (recur (process-input state (first inputs)) (drop 1 inputs) outputs)

    (= 4 (opcode (memory pointer))) 
      (recur (assoc state :pointer (+ 2 pointer)) 
             inputs 
             (conj outputs (process-output state)))

    (#{5 6} (opcode (memory pointer))) (recur (jump-if state) inputs outputs)

    :else (recur (do-operation state) inputs outputs)))


(defn simple-run [memory & inputs]
  (nth (run-singly (boot memory) (when inputs inputs) []) 1))


(defn collect-output [memory & inputs]
  (nth (run-singly (boot memory) (when inputs inputs) []) 0))


(comment "collect output demonstrated on input from aoc day 5"
  (def i
    (vec (map #(Integer/parseInt %)
       (->  "resources/inputday5.txt"
       slurp
       clojure.string/trim
       (clojure.string/split #",")))))

  (collect-output i 1)
  (collect-output i 5))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; run intcode computer multithreaded
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn- run-async 
  "given an initial state and in input channel, returns an output channel, and a final
  channel where the final state of the program on halting is dumped out"
  [state in]
  (let [out (chan) 
        final (chan (a/sliding-buffer 1))]
    (a/go-loop [{:keys [pointer memory] :as state} state]
      (cond
        (= 99 (opcode (memory pointer)))
        (do (>! final (if (:output state) (:output state) 0))
            (close! final) (close! out))

        (= 3  (opcode (memory pointer))) 
        (recur (process-input state (<! in)))

        (= 4  (opcode (memory pointer))) 
        (do (>! out (process-output state))
            (recur (assoc state
                          :pointer (+ 2 pointer)
                          :output (process-output state))))

        (#{5 6} (opcode (memory pointer))) (recur (jump-if state))

        :else (recur (do-operation state))))
    [out final]))


(defn- take-until-closed 
  "Given a channel, takes messages from that channel until it is closed
  then returns a sequence of all messages it receives"
  ([channel] (take-until-closed [] channel))
  ([result-seq channel]
   (let [message (<!! channel)]
     (if message 
       (recur (conj result-seq message) channel)
       result-seq))))


(defn collect-output-async [memory input] 
  (let [in (chan) 
        [out] (run-async (boot memory) in)]
    (when input (>!! in input))
    (take-until-closed out)))


(defn- run-with-setting [memory in setting]
  (let [[out final] (run-async (boot memory) in)]
    (>!! in setting)
    [out final]))


(defn async-amps [memory [p1 p2 p3 p4 p5] input]
  (let [in (chan)
        [o1] (run-with-setting memory in p1)
        [o2] (run-with-setting memory o1 p2)
        [o3] (run-with-setting memory o2 p3)
        [o4] (run-with-setting memory o3 p4)
        [out] (run-with-setting memory o4 p5)]
    (>!! in input)
    (<!! out))) 


(defn amps-looped [memory [p1 p2 p3 p4 p5] input]
  (let [in (chan)
        [o1] (run-with-setting memory in p1)
        [o2] (run-with-setting memory o1 p2)
        [o3] (run-with-setting memory o2 p3)
        [o4] (run-with-setting memory o3 p4)
        [out final] (run-with-setting memory o4 p5)]
    (a/pipe out in)
    (>!! in input)
    (<!! final)))


(defn find-max-amplification [function memory phases]
  (apply max (map #(function memory % 0) (combo/permutations phases))))


(comment 
  "day 7 answers"
  (def i
    (vec (map #(Integer/parseInt %)
              (->  "resources/inputday7.txt"
                   slurp
                   clojure.string/trim
                   (clojure.string/split #",")))))
  
  (find-max-amplification async-amps i #{0 1 2 3 4})
  (time (find-max-amplification amps-looped i #{5 6 7 8 9})))


(comment "useful testing initial mem states"
  (def in-mem [1 1 3 5 99 -1])
  (def in-out-mem [3 5 4 5 99 -1])
  (def immediate-halt-mem [99])
  (def return-two-things [4 5 4 6 99 123 321])
  (def adder [3 10 1 9 10 10 4 10 99 1 -1])
  (def add-two-numbers [3 0 3 2 1 0 2 0 4 0 99])
  (collect-output add-two-numbers 5 6))
