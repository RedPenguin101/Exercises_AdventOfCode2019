(ns adventofcode.intcode-emulator)

(defn deconstruct-opcode-value [number]
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


(defmethod do-instruction :input [program]
  "If the :inputs value of the program has instructions, it uses
  the first input. Otherwise it asks the user for input"

  (let [{:keys [pointer memory inputs]} program
        input-val (if (pos? (count inputs))
                    (first inputs)
                    (do (println "type your input") (Integer/parseInt (read-line))))]

    (assoc-in
      (assoc program :pointer (+ 2 pointer) :inputs (drop 1 inputs))
      [:memory (memory (+ 1 pointer))]
      input-val)))


(defmethod do-instruction :output [program]
  (let [{:keys [pointer memory phases current-phase]} program
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
  (vec (map #(Integer/parseInt %) (clojure.string/split (clojure.string/trim (slurp filename)) #","))))

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

(defn build-and-run [memory]
  (run (build-program memory)))

(defn run-with-noun-verb [noun verb filename]
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

(defn run-with-phases [memory phases]
  (run (add-phases (build-program memory) phases)))

