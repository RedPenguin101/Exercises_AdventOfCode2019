(ns adventofcode.intcode-emulator)

(defn deconstruct-opcode-value [number]
  (let [param-vals (quot number 100)
        opcode (- number (* param-vals 100))]
    [(reverse (map #(Character/digit % 10) (format "%03d" param-vals)))
     opcode]))

(defn get-instruction-params [instruction-pointer]
  [(+ 1 instruction-pointer)
   (+ 2 instruction-pointer)
   (+ 3 instruction-pointer)])

(defn dispatch-instruction [program]
  (cond
    (<= (:opcode program) 2) :addmult
    (= (:opcode program) 3) :input
    (= (:opcode program) 4) :output
    :else program))

(defmulti do-instruction dispatch-instruction)

(defmethod do-instruction :input [program]
  (println "type your input")
  (let [input (Integer/parseInt (read-line))
        memory (:memory program)
        position (memory (inc (:instruction-pointer program)))]
    (assoc memory position input)))

(defmethod do-instruction :output [program]
  (let [memory (:memory program)
        immed-mode (pos? (nth (:param-modes program) 0))
        param (inc (:instruction-pointer program))
        printloc (if immed-mode param (memory param))]
    (println (memory printloc))
    memory))

(defmethod do-instruction :addmult [program]
  (let [memory (:memory program)
        [pmode1 pmode2 _] (map #(= 1 %) (:param-modes program))
        [param1 param2 param3] (map memory (get-instruction-params (:instruction-pointer program)))
        x (if pmode1 param1 (memory param1))
        y (if pmode2 param2 (memory param2))]
    (assoc memory param3 (({1 + 2 *} (:opcode program)) x y))))

(defn run
  ([memory] (run 0 memory))
  ([instruction-pointer memory]
   (let [[param-modes opcode] (deconstruct-opcode-value (memory instruction-pointer))]
     (if (= opcode 99)
       memory
       (recur
         (+ instruction-pointer (if (<= opcode 2) 4 2))
         (do-instruction {:opcode opcode
                          :param-modes param-modes
                          :instruction-pointer instruction-pointer
                          :memory memory}))))))

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
