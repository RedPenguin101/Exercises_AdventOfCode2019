(ns adventofcode.manhattan-calc)

(defn move [current-pos cmd]
  (let [[x y] current-pos
        dir (subs cmd 0 1)
        steps (range 1 (inc (Integer/parseInt (subs cmd 1))))]
    (for [step steps]
      (case dir
        "R" [(+ x step) y]
        "L" [(- x step) y]
        "U" [x (+ y step)]
        "D" [x (- y step)]))))

(defn build-path [cmd-string]
  (drop
    1
    (reduce
      (fn [old-path cmd]
        (concat old-path (move (last old-path) cmd)))
      [[0 0]] (clojure.string/split cmd-string #","))))

(defn find-intersections [path1 path2]
  (vec (clojure.set/intersection
         (set path1)
         (set path2))))

(defn distance [point1 point2]
  0)

