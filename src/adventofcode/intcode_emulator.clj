(ns adventofcode.intcode-emulator)

(defn deconstruct-opcode-value [number]
  (let [param-vals (quot number 100)
        opcode (- number (* param-vals 100))]
    [(reverse (map #(Character/digit % 10) (format "%03d" param-vals))) opcode]))

(defn get-instruction-params [instruction-pointer]
  (vec (range (+ 1 instruction-pointer) (+ 4 instruction-pointer))))

(defn dispatch-instruction [{:keys [pointer memory]}]
  (let [opcode ((deconstruct-opcode-value (memory pointer)) 1)]
    (cond
      (<= opcode 2) :addmultcomp
      (<= 7 opcode 8) :addmultcomp
      (= opcode 3) :input
      (= opcode 4) :output
      (<= 5 opcode 6) :jump-if)))

(defmulti do-instruction dispatch-instruction)

(defmethod do-instruction :input [{:keys [pointer memory]}]
  (println "type your input")
  [(+ pointer 2) (assoc memory (memory (inc pointer)) (Integer/parseInt (read-line)))])

(defmethod do-instruction :output [{:keys [pointer memory]}]
  (let [[param-modes] (deconstruct-opcode-value (memory pointer))
        param (inc pointer)
        printloc (if (pos? (nth param-modes 0)) param (memory param))]
    (println (memory printloc))
    [(+ pointer 2) memory]))

(defmethod do-instruction :jump-if [{:keys [pointer memory]}]
  (let [[param-modes opcode] (deconstruct-opcode-value (memory pointer))
        immediate_modes (map #(= 1 %) param-modes)
        args (map memory (get-instruction-params pointer))
        [x jump-to] (map #(if %1 %2 (memory %2)) immediate_modes args)
        jump? (= (= 5 opcode) (not= 0 x))]
    [(if jump? jump-to (+ 3 pointer)) memory]))

(defn calc-new-pos-value [pointer memory]
  (let [[param-modes opcode] (deconstruct-opcode-value (memory pointer))
        immediate-modes (take 2 (map #(= 1 %) param-modes))
        args (map memory (get-instruction-params pointer))
        func ({1 + 2 * 7 < 8 =} opcode)
        new-pos-value (apply func (map #(if %1 %2 (memory %2)) immediate-modes args))]
    (case new-pos-value
      true 1
      false 0
      new-pos-value)))

(defmethod do-instruction :addmultcomp [{:keys [pointer memory]}]
  [(+ 4 pointer) (assoc memory (memory (+ 3 pointer)) (calc-new-pos-value pointer memory))])

(defn run
  ([memory] (run 0 memory))
  ([instruction-pointer memory]
   (if (= (memory instruction-pointer) 99)
     memory
     (let [[new-instr new-mem] (do-instruction {:pointer instruction-pointer :memory memory})]
       (recur new-instr new-mem)))))

(defn load-memory-state [filename]
  (vec (map #(Integer/parseInt %) (clojure.string/split (clojure.string/trim (slurp filename)) #","))))

(defn run-program [noun verb filename]
  (-> (load-memory-state filename)
      (assoc 1 noun)
      (assoc 2 verb)
      (run)
      (first)))

(defn find-output [output filename]
  (for [noun (range 100)
        verb (range 100)
        :let [result (run-program noun verb filename)]
        :when (= output result)]
    result))
