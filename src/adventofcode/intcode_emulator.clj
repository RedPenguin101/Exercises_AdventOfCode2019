(ns adventofcode.intcode-emulator)

(defn loc-calc [operation-pos]
  [(+ 1 operation-pos) (+ 2 operation-pos) (+ 3 operation-pos)])

(defn do-operation [func operation-pos code]
  (let [[x-loc y-loc replace-loc] (map code (loc-calc operation-pos))
        [x y] (map code [x-loc y-loc])]
    (assoc code replace-loc (func x y))))

(defn run
  ([input] (run 0 input))
  ([position input]
   (let [operation (input position)]
     (if (= operation 99)
       input
       (recur (+ position 4) (do-operation ({1 + 2 *} operation) position input))))))

(defn load-intcode [filename]
  (vec (map #(Integer/parseInt %) (clojure.string/split (clojure.string/trim (slurp filename)) #","))))

(defn run-intcode [filename]
  (run (load-intcode filename)))
