(ns adventofcode.intcode-emulator
  (:require [clojure.math.combinatorics :as combo]))

(defn deconstruct-opcode-value 
  "Given a long opcode of up to 5 digits, returns a vector of the param-values
   (a vector of the first three digits) and the opcode (the last two digits)"
  [number]
  (let [param-vals (quot number 100)
        opcode (- number (* param-vals 100))]
    [(reverse (map #(Character/digit % 10) (format "%03d" param-vals))) opcode]))

(defn get-instruction-params [pointer]
  (vec (range (+ 1 pointer) (+ 4 pointer))))

(defn calc-new-pos-value [{:keys [pointer memory]}]
  (let [[param-modes opcode] (deconstruct-opcode-value (memory pointer))
        immediate-modes (take 2 (map #(= 1 %) param-modes))
        args (map memory (get-instruction-params pointer))
        func ({1 + 2 * 7 < 8 =} opcode)
        new-pos-value (apply func (map #(if %1 %2 (memory %2)) immediate-modes args))]
    (case new-pos-value
      true 1
      false 0
      new-pos-value)))

;;;;
;; Dispatch and operations
;;;;

(defn dispatch-instruction [program]
  (let [opcode ((deconstruct-opcode-value ((:memory program) (:pointer program))) 1)]
    (cond
      (<= opcode 2) :operation
      (<= 7 opcode 8) :operation
      (= opcode 3) :input
      (= opcode 4) :output
      (<= 5 opcode 6) :jump-if)))

(defmulti do-instruction dispatch-instruction)


(defmethod do-instruction :operation [program]
  (let [{:keys [pointer memory]} program]
    (assoc-in
      (assoc program :pointer (+ 4 pointer))
      [:memory (memory (+ 3 pointer))]
      (calc-new-pos-value program))))


(defmethod do-instruction :input
  input-instruction
  [{:keys [pointer memory inputs] :as program}]
  "If the :input value of the program has instructions, it uses
  the first input. Otherwise it asks the user for input"

  (let [input-val (if (pos? (count inputs))
                    (first inputs)
                    (do (println "type your input") (Integer/parseInt (read-line))))]

    (assoc-in
     (assoc program :pointer (+ 2 pointer) :inputs (drop 1 inputs))
     [:memory (memory (+ 1 pointer))]
     input-val)))


(defmethod do-instruction :output [program]
  (let [{:keys [pointer memory]} program
        [param-modes] (deconstruct-opcode-value (memory pointer))
        param (inc pointer)
        output-loc (if (pos? (nth param-modes 0)) param (memory param))]

    ;;(println (memory output-loc))
    (assoc program :pointer (+ 2 pointer) :output (memory output-loc))))


(defmethod do-instruction :jump-if [program]
  (let [{:keys [pointer memory]} program
        [param-modes opcode] (deconstruct-opcode-value (memory pointer))
        immediate_modes (map #(= 1 %) param-modes)
        args (map memory (get-instruction-params pointer))
        [x jump-to-pos] (map #(if %1 %2 (memory %2)) immediate_modes args)
        jump? (= (= 5 opcode) (not= 0 x))]
    (assoc program :pointer (if jump? jump-to-pos (+ 3 pointer)))))

;;;;
;; program building
;;;;

(defn load-memory-state [filename]
  (vec (map #(Integer/parseInt %) 
            (clojure.string/split (clojure.string/trim (slurp filename)) #","))))

(defn build-program [memory & rest]
  (let [base {:pointer 0 :memory memory}]
    (if rest
      (assoc base :inputs (nth rest 0))
      base)))

(defn add-phases [program phases]
  (assoc program :current-phase 0 :phases phases))

;;;;
;; running programs
;;;;

(defn run
  "Takes a program, and if the program has a terminate instruction
  (99) it returns the program. Otherwise it executes the next
  instruction"
  [program]
  (if (= ((:memory program) (:pointer program)) 99)
    program
    (recur (do-instruction program))))

;;;;
;; Higher level uses
;;;;

(defn run-with-noun-verb 
  "Runs the intcode computer with a 'noun' and 'verb' input, putting them in
  the 1 and 2 positions of memory respectively before running the program.
  The output is the value in memory position 0 when the system halts."
  [noun verb filename]
  (-> (load-memory-state filename)
      (assoc 1 noun)
      (assoc 2 verb)
      (build-program)
      (run)
      (:memory)
      (first)))

(defn find-output [output filename]
  (for [noun (range 100)
        verb (range 100)
        :let [result (run-with-noun-verb noun verb filename)]
        :when (= output result)]
    result))

(defn amplifier-controller 
  "Runs the int-code software "
  [memory input phase-settings]
  (let [output (:output (run (build-program memory [(first phase-settings) input])))]
    (if (= 1 (count phase-settings))
      output
      (recur memory output (drop 1 phase-settings)))))

(defn find-max-amplification [memory]
  (apply max (map #(amplifier-controller memory 0 %) (combo/permutations [0 1 2 3 4]))))

(find-max-amplification [3,15,3,16,1002,16,10,16,1,16,15,15,4,15,99,0,0])
