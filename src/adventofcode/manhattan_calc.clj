(ns adventofcode.manhattan-calc
  (:require clojure.set))

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
  (drop 1 (reduce
            (fn [old-path cmd]
              (concat old-path (move (last old-path) cmd)))
            [[0 0]] (clojure.string/split cmd-string #","))))

(defn find-intersections [path1 path2]
  (vec (clojure.set/intersection (set path1) (set path2))))

(defn distance [point1 point2]
  (reduce + (map #(Math/abs (- %1 %2)) point1 point2)))

(defn min-man-distance [points]
  (apply min (map #(distance [0 0] %) points)))

(defn path-distance-to [path point]
  (inc (count (take-while #(not= % point) path))))

(defn min-path-distance [path1 path2]
  (apply min (for [intersection-point (find-intersections path1 path2)]
         (+ (path-distance-to path1 intersection-point)
            (path-distance-to path2 intersection-point)))))
