(ns adventofcode.manhattan-calc)

;; find the intersection point closest to the central port.

;; Manhattan
;; https://en.wikipedia.org/wiki/Taxicab_geometry
;; sum of absloute values of pi-qi where and q are vectors


;; instruction is U5 etc, path is vec of l2 vectors
(defn calc-next-coord [coord instruction]
  (let [[direction mag-string] (re-seq #"[a-zA-Z0-9]" instruction)
        mag (Integer/parseInt mag-string)]
    (case direction
      "U" (vector (first coord) (+ (second coord) mag))
      "D" (vector (first coord) (- (second coord) mag))
      "R" (vector (+ (first coord) mag) (second coord))
      "L" (vector (- (first coord) mag) (second coord)))))

(defn update-path [path instruction]
  (conj path (calc-next-coord (last path) instruction)))

(defn find-intersections [path1 path2])

(defn manhattan-distance [coord])


