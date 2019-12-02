(ns adventofcode.intcode-emulator)

(def ops {1 + 2 *})

(defn loc-calc [operation-pos]
  [(+ 1 operation-pos) (+ 2 operation-pos) (+ 3 operation-pos)])

(defn operation [func operation-pos code]
  (let [[x-loc y-loc replace-loc] (map code (loc-calc operation-pos))
        [x y] (map code [x-loc y-loc])]
    (assoc code replace-loc (func x y))))

(defn run-intcode
  ([code] (run-intcode 0 code))
  ([start-pos code]
   (cond
     (= 1 (code start-pos)) (run-intcode (+ 4 start-pos) (operation + start-pos code))
     (= 2 (code start-pos)) (run-intcode (+ 4 start-pos) (operation * start-pos code))
     (= 99 (code start-pos)) code)))
