(ns adventofcode.intcode-async
  (:require [clojure.math.combinatorics :as combo]
            [clojure.core.async :as a
                                :refer [>!! >! <!! <! chan go close!]]))

(defn- boot [memory]
  {:memory memory :pointer 0})

(defn deconstruct-opcode-value 
  "Given a long opcode of up to 5 digits, returns a vector of the param-values
   (a vector of the first three digits) and the opcode (the last two digits)"
  [number]
  (let [param-vals (quot number 100)]
    [(reverse (map #(Character/digit % 10) (format "%03d" param-vals)))
     (- number (* param-vals 100))]))

(defn opcode [number]
  ((deconstruct-opcode-value number) 1))

(defn immediate? [{:keys [memory pointer] :as state} position]
  (= 1 (nth ((deconstruct-opcode-value (memory pointer)) 0) position)))

(defn calc-new-pos-value [{:keys [memory pointer] :as state}]
  ;(println "calc" state)
  (({1 + 2 *} (opcode (memory pointer))) 
   ((if (immediate? state 0) identity memory) (memory (+ pointer 1))) 
   ((if (immediate? state 1) identity memory) (memory (+ pointer 2)))))

(comment "below gives example"
  (def s {:memory [1002 4 3 4 33] :pointer 0})
  (def m (:memory s))
  (def p (:pointer s))
  
  ((if (immediate? s 0) identity m) (m (+ p 1))) ;=> 33, because in pos mode
  ((if (immediate? s 1) identity m) (m (+ p 2))) ;=> 3 in immediate mode
  (calc-new-pos-value s) ;=> 99
  )

(defn do-operation [{:keys [pointer, memory] :as state}]
  ;(println "op" state)
  (-> state
      (assoc :pointer (+ 4 pointer))
      (assoc-in [:memory (memory (+ 3 pointer))] 
                (calc-new-pos-value state))))

(defn process-input [{:keys [pointer, memory] :as state} input]
  ;(println "in" state "input" input)
  (-> state
      (assoc :pointer (+ 2 pointer))
      (assoc-in [:memory (memory (+ 1 pointer))] input)))

(defn process-output [{:keys [pointer, memory] :as state}]
  ;(println "out" state)
  (memory (memory (+ 1 pointer))))

(defn run-singly [{:keys [pointer memory] :as state} inputs outputs]
  (cond
    (= 99 (opcode (memory pointer))) state

    (= 3 (opcode (memory pointer))) 
      (recur (process-input state (first inputs)) (drop 1 inputs) outputs)

    (= 4 (opcode (memory pointer))) 
      (recur (assoc state :pointer (+ 2 pointer)) 
             inputs 
             (conj outputs (process-output state)))

    :else (recur (do-operation state) inputs outputs)))

(defn run [{:keys [pointer memory] :as state} in]
  (let [out (chan) 
        final (chan (a/sliding-buffer 1))]
    (a/go-loop [{:keys [pointer memory] :as state} state]
      (cond
        (= 99 (opcode (memory pointer))) (do (>! final state) (close! final) (close! out))
        (= 3 (opcode (memory pointer))) (recur (process-input state (<! in)))
        (= 4 (opcode (memory pointer))) (do (>! out (process-output state))
                                   (recur (assoc state :pointer (+ 2 pointer))))
        :else (recur (do-operation state))))
    [out final]))

(defn simple-run [memory & inputs]
  (run-singly (boot memory) (when inputs inputs) []))

(defn- take-until-closed 
  "Given a channel, takes messages from that channel until it is closed
  then returns a sequence of all messages it receives"
  ([channel] (take-until-closed [] channel))
  ([result-seq channel]
   (let [message (<!! channel)]
     (if message 
       (recur (conj result-seq message) channel)
       result-seq))))

(defn collect-output [memory input] 
  (let [in (chan) 
        [out] (run (boot memory) in)]
    (when input (>!! in input))
    (take-until-closed out)))

(comment
  (def in-mem [1 1 3 5 99 -1])
  (def in-out-mem [3 5 4 5 99 -1])
  (def immediate-halt-mem [99])
  (def return-two-things [4 5 4 6 99 123 321])
  (def adder [3 10 1 9 10 10 4 10 99 1 -1])

  (collect-output adder 10)
  (collect-output return-two-things nil)
  (collect-output immediate-halt-mem 10)
  (simple-run in-mem)

  (let [in (chan) 
        o1 (run (boot in-out-mem) in)
        out (run (boot in-out-mem) o1)]
    (>!! in 10)
    (<!! out))
  
  (let [in (chan) 
        [middle] (run (boot adder) in)
        [out] (run (boot adder) middle)]
    (>!! in 5)
    (<!! out))  

  (let [in (chan)
        [out] (run (boot adder) in)]
    (>!! in 5)
    (take-until-closed out))
  
  )

