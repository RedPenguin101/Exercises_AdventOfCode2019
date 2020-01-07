(ns adventofcode.intcode-async
  (:require [clojure.math.combinatorics :as combo]
            [clojure.core.async :as a
                                :refer [>!! >! <!! <! chan go close!]]))

(comment
  "program operations pass the program state around
  memory is the main thing
  pointer is the pointer to the current piece of memory under cursor
  in-chan and out-chan are the channels the computer looks for input
  and puts its output")

;; this program takes input and immediately passes it out

(defn boot [memory]
  {:memory memory :pointer 0})

(defn calc-new-pos-value [{:keys [memory pointer] :as state}]
  ;(println "calc" state)
  (({1 + 2 *} (memory pointer)) 
   (memory (memory (+ pointer 1))) 
   (memory (memory (+ pointer 2)))))

(defn do-operation [{:keys [pointer, memory] :as state}]
  ;(println "op" state)
  (-> state
      (assoc :pointer (+ 4 pointer))
      (assoc-in [:memory (memory (+ 3 pointer))] (calc-new-pos-value state))))

(defn process-input [{:keys [pointer, memory] :as state} input]
  ;(println "in" state "input" input)
  (-> state
      (assoc :pointer (+ 2 pointer))
      (assoc-in [:memory (memory (+ 1 pointer))] input)))

(defn process-output [{:keys [pointer, memory] :as state}]
  ;(println "out" state)
  (memory (memory (+ 1 pointer))))

(defn run [{:keys [pointer memory] :as state} in]
  (let [out (chan) 
        final (chan (a/sliding-buffer 1))]
    (a/go-loop [{:keys [pointer memory] :as state} state]
      (cond
        (= 99 (memory pointer)) (do  (>! final state) (close! final) (close! out))
        (= 3 (memory pointer)) (recur (process-input state (<! in)))
        (= 4 (memory pointer)) (do (>! out (process-output state))
                                   (recur (assoc state :pointer (+ 2 pointer))))
        :else (recur (do-operation state))))
    [out final]))

(defn simple-run [memory]
  (let [[_ final] (run (boot memory) nil)]
  (<!! final)))

(def in-out-mem [3 5 4 5 99 -1])

(defn collect-output [memory input] 
  (let [in (chan) 
       [out _] (run (boot in-out-mem) in)]
    (>!! in input)
    (take 1 out)))

(def adder [3 10 1 9 10 10 4 10 99 1 -1])


(comment

  (collect-output adder 10)

  (def in-mem [1 1 3 5 99 -1])
  (def in-out-mem [3 5 4 5 99 -1])
  (def immediate-halt-mem [99])
  
  (let [in (chan) 
        o1 (run (boot in-out-mem) in)
        out (run (boot in-out-mem) o1)]
    (>!! in 10)
    (<!! out))
  
  (let [in (chan) 
        middle (run (boot adder) in)
        out (run (boot adder) middle)]
    (>!! in 5)
    (<!! out))  
  
  
  (simple-run in-mem))

