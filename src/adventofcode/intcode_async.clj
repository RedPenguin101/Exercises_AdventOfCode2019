(ns adventofcode.intcode-async
  (:require [clojure.math.combinatorics :as combo]
            [clojure.core.async :as a
                                :refer [>!! >! <!! <! chan go]]))

(comment
  "program operations pass the program state around
  memory is the main thing
  pointer is the pointer to the current piece of memory under cursor
  in-chan and out-chan are the channels the computer looks for input
  and puts its output")

;; this program takes input and immediately passes it out
(def in-mem [1 1 3 5 99 -1])
(def in-out-mem [3 5 4 5 99 -1])
(def immediate-halt-mem [99])
(def adder [3 10 1 9 10 10 4 10 99 1 -1])

(defn boot [memory]
  {:memory memory :pointer 0})

(defn calc-new-pos-value [{:keys [memory pointer] :as state}]
  (-> state
      (assoc :pointer (+ 4 pointer)) 
      (assoc-in [:memory 10] 123)))

(defn do-operation [{:keys [pointer] :as state}]
  (-> state
      (assoc :pointer (+ 4 pointer))
      (assoc-in [:memory (+ 3 pointer)] (calc-new-pos-value state))))

(defn process-input [{:keys [pointer, memory] :as program} input]
  (-> program
      (assoc :pointer (+ 2 pointer))
      (assoc-in [:memory (memory (+ 1 pointer))] input)))

(defn process-output [{:keys [pointer, memory] :as state}]
  (println state)
  (memory (memory (+ 1 pointer))))

(defn run [{:keys [pointer memory] :as state} in out]
  (a/go-loop [{:keys [pointer memory] :as state} state]
    (cond
      (= 99 (memory pointer)) (println "done")
      (= 3 (memory pointer)) (recur (process-input state (<! in)))
      (= 4 (memory pointer)) (do (>! out (process-output state))
                                 (recur (assoc state :pointer (+ 2 pointer))))
      :else (recur (do-operation state)))))

(let [in (chan) 
      middle (chan)
      out (chan)]
  (run (boot in-out-mem) in middle)
  (run (boot in-out-mem) middle out)
  (>!! in 10)
  (<!! out)
  )

(let [in (chan) 
      out (chan)]
  (run (boot adder) in out)
  (>!! in 5)
  (<!! out))  
