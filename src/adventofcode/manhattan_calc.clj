(ns adventofcode.manhattan-calc)

;; find the intersection point closest to the central port.

;; Manhattan
;; https://en.wikipedia.org/wiki/Taxicab_geometry
;; sum of absloute values of pi-qi where and q are vectors


;; instruction is U5 etc, path is vec of l2 vectors
(defn calc-end [coord instruction]
  (let [direction (re-find #"[A-Z]" instruction)
        mag (Integer/parseInt (re-find #"\d+" instruction))]
    (case direction
      "U" (vector (first coord) (+ (second coord) mag))
      "D" (vector (first coord) (- (second coord) mag))
      "R" (vector (+ (first coord) mag) (second coord))
      "L" (vector (- (first coord) mag) (second coord)))))

(defn create-line [start-coord instruction]
  {:start start-coord :end (calc-end start-coord instruction)})

(defn next-line [lines instruction]
  (let [new-start (:end (last lines))
        new-end (calc-end new-start instruction)]
    (conj lines {:start new-start :end new-end})))

(defn build-path [instructions]
  (reduce next-line
          [{:start [0 0] :end (calc-end [0 0] (first instructions))}]
          (rest instructions)))

(defn is-hor? [line]
  (= ((:start line) 1) ((:end line) 1)))

(defn intersect? [line1 line2]
  (let [a1 (:start line1) a2 (:end line1)
        b1 (:start line2) b2 (:end line2)]
    (and
      (not= (> (a1 0) (b1 0)) (> (a2 0) (b2 0)))
      (not= (> (a1 1) (b1 1)) (> (a2 1) (b2 1))))))

(defn manhattan-distance [vec1 vec2]
  (reduce + (map #(Math/abs (- %1 %2)) vec1 vec2)))

(defn find-intersection* [line1 line2]
  (let [hoz-line (if (is-hor? line1) line1 line2)
        vert-line (if-not (is-hor? line1) line1 line2)
        x ((:start vert-line) 0)
        y ((:start hoz-line) 1)]
    {:intersect [x y] :m-distance (manhattan-distance [0 0] [x y])}))

(defn find-intersection [line1 line2]
  (cond
    (apply = (map is-hor? [line1 line2])) nil
    (intersect? line1 line2) (find-intersection* line1 line2)
    :else nil))

(defn find-intersections [path1 path2]
  (remove nil? (for [a path1 b path2] (find-intersection a b))))

(defn min-manhatten-intersection [path1 path2]
  (apply min (map :m-distance (remove nil? (find-intersections path1 path2)))))

(defn str->instrs [string]
  (clojure.string/split string #","))

(defn length [line]
  (manhattan-distance (:start line) (:end line)))

