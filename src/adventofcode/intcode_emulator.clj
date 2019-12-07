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
      (<= 5 opcode 6) :jump-if
      (<= 7 opcode 8) :comparator
      :else program)))

(defmulti do-instruction dispatch-instruction)

(defmethod do-instruction :jump-if [program]
  (let [memory (:mem program)
        pointer (:pointer program)
        [param-modes opcode] (deconstruct-opcode-value (memory pointer))
        jump-if-true? (= 5 opcode)
        [im-mode1? im-mode2? _] (map #(= 1 %) param-modes)
        [arg1 arg2 _] (map memory (get-instruction-params pointer))
        x (if im-mode1? arg1 (memory arg1))
        jump-to (if im-mode2? arg2 (memory arg2))
        is-true? (not= 0 x)
        jump? (= jump-if-true? is-true?)]
    [(if jump? jump-to (+ 3 pointer)) memory]))

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
        immed-mode? (pos? (nth param-modes 0))
        param (inc (:pointer program))
        printloc (if immed-mode? param (memory param))]
    (println (memory printloc))
    [(+ (:pointer program) 2) memory]))

(defmethod do-instruction :addmult [program]
  (let [memory (:mem program)
        pointer (:pointer program)
        [param-modes opcode] (deconstruct-opcode-value (memory pointer))
        [im-mode1? im-mode2? _] (map #(= 1 %) param-modes)
        [arg1 arg2 pos-to-change] (map memory (get-instruction-params pointer))
        x (if im-mode1? arg1 (memory arg1))
        y (if im-mode2? arg2 (memory arg2))
        func ({1 + 2 *} opcode)
        new-pos-val (func x y)]
    [(+ 4 pointer) (assoc memory pos-to-change new-pos-val)]))

(defmethod do-instruction :comparator [program]
  (let [memory (:mem program)
        pointer (:pointer program)
        [param-modes opcode] (deconstruct-opcode-value (memory pointer))
        [im-mode1? im-mode2? _] (map #(= 1 %) param-modes)
        [arg1 arg2 pos-to-change] (map memory (get-instruction-params pointer))
        x (if im-mode1? arg1 (memory arg1))
        y (if im-mode2? arg2 (memory arg2))
        func ({7 < 8 =} opcode)
        new-pos-val (if (func x y) 1 0)]
    [(+ 4 pointer) (assoc memory pos-to-change new-pos-val)]))

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
