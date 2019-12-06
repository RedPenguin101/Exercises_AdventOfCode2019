(ns adventofcode.intcode-emulator)

(defn deconstruct-opcode-value [number]
  (let [param-vals (quot number 100)
        opcode (- number (* param-vals 100))]
    [(reverse (map #(Character/digit % 10) (format "%03d" param-vals)))
     opcode]))

(defn get-instruction-params [instruction-pointer]
  [(+ 1 instruction-pointer) (+ 2 instruction-pointer) (+ 3 instruction-pointer)])

(defn dispatch-instruction [program]
  (cond
    (<= (:opcode program) 2) :addmult
    ))

(defmulti do-instruction dispatch-instruction)

(defmethod do-instruction :addmult [program]
  (let [memory (:memory program)
        [param1 param2 param3] (map memory (get-instruction-params (:instruction-pointer program)))
        [x y] (map memory [param1 param2])]
    (assoc memory param3 (({1 + 2 *} (:opcode program)) x y))))

(defn run
  ([memory] (run 0 memory))
  ([instruction-pointer memory]
   (let [[param-modes opcode] (deconstruct-opcode-value (memory instruction-pointer))]
     (if (= opcode 99)
       memory
       (recur
         (+ instruction-pointer 4)
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
