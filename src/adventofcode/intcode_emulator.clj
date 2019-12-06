(ns adventofcode.intcode-emulator)

(defn deconstruct-opcode-value [number]
  (let [param-vals (quot number 100)
        opcode (- number (* param-vals 100))]
    [(reverse (map #(Character/digit % 10) (format "%03d" param-vals)))
     opcode]))

(defn get-instruction-params [instruction-pointer]
  [(+ 1 instruction-pointer) (+ 2 instruction-pointer) (+ 3 instruction-pointer)])

(defn do-instruction [func instruction-pointer memory]
  (let [[param1 param2 param3] (map memory (get-instruction-params instruction-pointer))
        [x y] (map memory [param1 param2])]
    (assoc memory param3 (func x y))))

(defn run
  ([memory] (run 0 memory))
  ([instruction-pointer memory]
   (let [opcode (memory instruction-pointer)]
     (if (= opcode 99)
       memory
       (recur
         (+ instruction-pointer 4)
         (do-instruction ({1 + 2 *} opcode) instruction-pointer memory))))))

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
