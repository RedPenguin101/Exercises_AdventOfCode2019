(ns adventofcode.intcode-emulator)

(defn deconstruct-opcode-value [number]
  (let [param-vals (quot number 100)
        opcode (- number (* param-vals 100))]
    [(reverse (map #(Character/digit % 10) (format "%03d" param-vals)))
     opcode]))

(defn get-instruction-params [instruction-pointer]
  (vec (range (+ 1 instruction-pointer) (+ 5 instruction-pointer))))

(defn dispatch-instruction [program]
  (let [opcode ((deconstruct-opcode-value ((:mem program) (:pointer program))) 1)]
    (cond
      (<= opcode 2) :addmult
      (= opcode 3) :input
      (= opcode 4) :output
      (= opcode 5) :jump-if-true
      :else program)))

(defmulti do-instruction dispatch-instruction)

(defmethod do-instruction :jump-if-true [program]
  (let [memory (:mem program)
        pointer (:pointer program)
        jump? (not= 0 (memory (inc pointer)))
        jump-to (memory (+ 2 pointer))
        new-instr (if jump? jump-to (+ pointer 3))]
    [new-instr memory]))

(defmethod do-instruction :input [program]
  (println "type your input")
  (let [input (Integer/parseInt (read-line))
        memory (:mem program)
        position (memory (inc (:pointer program)))]
    [(+ (:pointer program) 2) 
     (assoc memory position input)]))

(defmethod do-instruction :output [program]
  (let [memory (:mem program)
        [param-modes _] (deconstruct-opcode-value ((:mem program) (:pointer program)))
        immed-mode (pos? (nth param-modes 0))
        param (inc (:pointer program))
        printloc (if immed-mode param (memory param))]
    (println (memory printloc))
    [(+ (:pointer program) 2) memory]))

(defmethod do-instruction :addmult [program]
  (let [memory (:mem program)
        [param-modes opcode] (deconstruct-opcode-value ((:mem program) (:pointer program)))
        [pmode1 pmode2 _] (map #(= 1 %) param-modes)
        [param1 param2 param3] (map memory (get-instruction-params (:pointer program)))
        x (if pmode1 param1 (memory param1))
        y (if pmode2 param2 (memory param2))]
    [(+ 4 (:pointer program)) (assoc memory param3 (({1 + 2 *} opcode) x y))]))

(defn run
  ([memory] (run 0 memory))
  ([instruction-pointer memory] 
   (if (= (memory instruction-pointer) 99)
     memory
     (let [[new-instr new-mem] (do-instruction {:pointer instruction-pointer :mem memory})]
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
    {:result result :noun noun :verb verb :noun-verb-comb (+ (* 100 noun) verb)}))
