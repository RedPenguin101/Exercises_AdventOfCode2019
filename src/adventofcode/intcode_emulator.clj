(ns adventofcode.intcode-emulator)

(defn deconstruct-opcode-value [number]
  (let [param-vals (quot number 100)
        opcode (- number (* param-vals 100))]
    [(reverse (map #(Character/digit % 10) (format "%03d" param-vals))) opcode]))

(defn get-instruction-params [pointer]
  (vec (range (+ 1 pointer) (+ 4 pointer))))

(defn dispatch-instruction [program]
  (let [opcode ((deconstruct-opcode-value ((:memory program) (:pointer program))) 1)]
    (cond
      (<= opcode 2) :operation
      (<= 7 opcode 8) :operation
      (= opcode 3) :input
      (= opcode 4) :output
      (<= 5 opcode 6) :jump-if)))

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

(defmulti do-instruction dispatch-instruction)

(defmethod do-instruction :operation [program]
  (let [{:keys [pointer memory]} program]
    (assoc-in
      (assoc program :pointer (+ 4 pointer))
      [:memory (memory (+ 3 pointer))]
      (calc-new-pos-value program))))

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
        [x jump-to-pos] (map #(if %1 %2 (memory %2)) immediate_modes args)
        jump? (= (= 5 opcode) (not= 0 x))]
    [(if jump? jump-to-pos (+ 3 pointer)) memory]))

(defn run
  [program]
  (if (= ((:memory program) (:pointer program)) 99)
    (:memory program)
    (recur (do-instruction program))))

(defn load-memory-state [filename]
  (vec (map #(Integer/parseInt %) (clojure.string/split (clojure.string/trim (slurp filename)) #","))))

(defn build-program [memory]
  {:pointer 0 :memory memory})

(defn run-with-noun-verb [noun verb filename]
  (-> (load-memory-state filename)
      (assoc 1 noun)
      (assoc 2 verb)
      (build-program)
      (run)
      (first)))

(defn find-output [output filename]
  (for [noun (range 100)
        verb (range 100)
        :let [result (run-with-noun-verb noun verb filename)]
        :when (= output result)]
    result))
