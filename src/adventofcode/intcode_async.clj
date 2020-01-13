(ns adventofcode.intcode-async
  (:require [clojure.math.combinatorics :as combo]
            [clojure.core.async :as a
                                :refer [>!! >! <!! <! chan close!]]))


(defn- boot [memory]
  {:memory memory :pointer 0})


(defn- modes-vec-from [modes]
  (vec (reverse (map #({0 :pos 1 :imm 2 :rel} (Character/digit % 10)) (format "%03d" modes)))))


(defn- deconstruct-instruction
  [instruction]
  (let [modes (quot instruction 100)]
    [(modes-vec-from modes) (- instruction (* modes 100))]))


; kill
(defn- apply-modes [instruction]
  (let [modes (get-in instruction [0 0])]
    {:opcode (get-in instruction [0 1])
     :args [[(modes 0) (instruction 1)] 
            [(modes 1) (instruction 2)]]
     :put-to (get instruction 3 nil)}))


; kill
(defn- function-inputs [{:keys [memory pointer]}]
  (-> (vec (map memory 
                (range pointer 
                       (+ (min 4 (- (count memory) pointer)) 
                          pointer))))
      (update 0 deconstruct-instruction)
      apply-modes))


(defn apply-rel-base [args rel-base]
  ;(println args rel-base)
  (vec (map 
        (fn [arg]
          (if (= (arg 0) :rel)
            [:pos (+ (arg 1) (if (nil? rel-base) 0 rel-base))]
            arg))
        args)))

(comment
  (apply-rel-base [[:rel 4] [:pos 5] [:imm 3]] 3)
  ;; => [[:pos 7] [:pos 5] [:imm 3]]
  )

(defn- apply-modes2 [instruction]
  (let [modes (get-in instruction [0 0])]
    {:opcode (get-in instruction [0 1])
     :args (map #(vector %1 %2) modes (drop 1 instruction))}))


(defn- function-inputs2 [{:keys [memory, pointer, rel-base]}]
  (-> (vec (map memory
                (range pointer
                       (+ (min 4 (- (count memory) pointer))
                          pointer))))
      (update 0 deconstruct-instruction)
      (apply-modes2)
      (update :args apply-rel-base rel-base)))

(comment
  (function-inputs2 {:memory [2101 4 5 4 99] :pointer 0 :rel-base 3})
  ;; => {:opcode 1, :args [[:imm 4] [:pos 8] [:pos 4]]}
  )

(defn- expand-memory [memory location]
  (let [size-gap (- location (count memory))]
    (if (pos? size-gap)
      (vec (concat memory (take (inc size-gap) (repeat 0))))
      memory)))

; kill - neeed to kill output too!
(defn- arg-val [memory arg & [rel-base]]
  ;(println memory arg rel-base)
  (cond
    (= (arg 0) :imm) (arg 1)
    (= (arg 0) :pos) ((expand-memory memory (arg 1)) (arg 1))
    (= (arg 0) :rel) ((expand-memory memory (arg 1)) (+ (if (nil? rel-base) 0 rel-base) (arg 1)))))


(defn- bool->int [input]
  (case input
    true 1
    false 0
    input))


(defn- operation-result [opcode [a1 a2]]
  (bool->int (({1 + 2 * 7 < 8 =} opcode) a1 a2)))


(defn- get-op-input [arg memory]
  "given an argument like [:pos 6] and a memory state, returns the arg value if in :imm
  mode, or the value at that memory position if in :pos mode
  if the position doesn't exist in memory, return 0"
  (if (= :imm (arg 0))
    (arg 1)
    (if (< (arg 1) (count memory)) 
      (memory (arg 1))
      0)))

(defn- do-operation [opcode args {:keys [memory pointer] :as state}]
  ;(println state args (count (:memory state)))
  
  (let [arg-val1 (get-op-input (args 0) memory)
        arg-val2 (get-op-input (args 1) memory)
        put-to (get-in args [2 1])]
    (-> state
        (assoc :pointer (+ 4 pointer) 
               :memory (expand-memory memory put-to))
        (assoc-in [:memory put-to] (operation-result opcode [arg-val1 arg-val2])))))


(defn- jump-if2 [opcode args2 {:keys [memory pointer] :as state}]
  (let [jump? (= (= 5 opcode) (not= 0 (get-op-input (args2 0) memory)))]
    (assoc state 
           :pointer 
           (if jump? 
             (get-op-input (args2 1) memory) 
             (+ 3 pointer)))))


(defn- process-input [args state input]
  ;(println args input state)
  (-> state
      (assoc :pointer (+ 2 (:pointer state)))
      (assoc-in [:memory
                 (get-in args [0 1])]
                input)))

(comment
  (process-input
   [[:pos 5] [:pos 4] [:pos -1]]
   {:memory [9 0 203 -4 4 -1 99], :pointer 2, :rel-base 9}
   0)
  ;; => {:memory [9 0 203 -4 4 0 99], :pointer 4, :rel-base 9}
  )


(defn- process-output [{:keys [memory] :as state}]
  ;(println "out" state)
  (arg-val memory ((:args (function-inputs state)) 0) (get state :rel-base 0)))


(defn- update-rel-base [[arg] state]
  (if (= (arg 0) :imm)
    (assoc state
           :rel-base (+ (get arg 1) (get state :rel-base 0))
           :pointer (+ 2 (:pointer state)))
    (assoc state
           :rel-base (+ ((:memory state) (get arg 1)) (get state :rel-base 0))
           :pointer (+ 2 (:pointer state)))))

(comment
  (update-rel-base [[:pos 5]] {:memory [9 5 0 0 0 10] :pointer 0})
  ;; => {:memory [9 5 0 0 0 10], :pointer 2, :rel-base 10}

  (update-rel-base [[:imm 5]] {:memory [9 5 0 0 0 10] :pointer 0})
  ;; => {:memory [9 5 0 0 0 10], :pointer 2, :rel-base 5}
  )
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; run intcode computer single threaded
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


(defn- run-singly [{:keys [pointer] :as state} inputs outputs]
  ;(println "========================================================================")
  ;(println state inputs outputs (count (:memory state)))
  (let [{:keys [opcode args]} (function-inputs2 state)] 
    (cond
      (= 99 opcode) [outputs state]

      (= 3 opcode) (recur (process-input args state (first inputs)) (drop 1 inputs) outputs)

      (= 4 opcode) (recur (assoc state :pointer (+ 2 pointer)) 
                          inputs 
                          (conj outputs (process-output state)))

      (#{5 6} opcode) (recur (jump-if2 opcode args state) inputs outputs)
      
      (= 9 opcode) (recur (update-rel-base args state) inputs outputs)

      :else (recur (do-operation opcode args state) inputs outputs))))


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


(comment
  "day 9 answers"
  (def i
    (vec (map #(Integer/parseInt %)
              (->  "resources/inputday9.txt"
                   slurp
                   clojure.string/trim
                   (clojure.string/split #",")))))

  (collect-output i 1)
  ;; => [2671328082]

  (collect-output i 2)
  ;; => [59095]
  )

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; run intcode computer multithreaded
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn- run-async 
  "given an initial state and in input channel, returns an output channel, and a final
  channel where the final state of the program on halting is dumped out"
  [state in]
  (let [out (chan) 
        final (chan (a/sliding-buffer 1))]
    (a/go-loop [{:keys [pointer] :as state} state]
      (let [{:keys [opcode args]} (function-inputs2 state)]
        (cond
          (= 99 opcode) (do (>! final (if (:output state) (:output state) 0))
                            (close! final) (close! out))

          (= 3  opcode) (recur (process-input args state (<! in)))

          (= 4  opcode) (do (>! out (process-output state))
                            (recur (assoc state
                                          :pointer (+ 2 pointer)
                                          :output (process-output state))))

          (#{5 6} opcode) (recur (jump-if2 opcode args state))

          :else (recur (do-operation opcode args state)))))
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


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; results
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(comment 
  "day 7 answers"
  (def i
    (vec (map #(Integer/parseInt %)
              (->  "resources/inputday7.txt"
                   slurp
                   clojure.string/trim
                   (clojure.string/split #",")))))
  
  (find-max-amplification async-amps i #{0 1 2 3 4})
  (time (find-max-amplification amps-looped i #{5 6 7 8 9}))
  )


(comment "useful testing initial mem states"
  (def in-mem [1 1 3 5 99 -1])
  (def in-out-mem [3 5 4 5 99 -1])
  (def immediate-halt-mem [99])
  (def return-two-things [4 5 4 6 99 123 321])
  (def adder [3 10 1 9 10 10 4 10 99 1 -1])
  (def add-two-numbers [3 0 3 2 1 0 2 0 4 0 99])
  (collect-output add-two-numbers 5 6))
