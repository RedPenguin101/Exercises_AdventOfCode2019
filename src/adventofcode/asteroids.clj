(ns adventofcode.asteroids)

(defn- get-input [string]
  (remove #(= "\n" %) (clojure.string/split string #"")))

(defn- asteroid-co-ords 
  "Given a sequence of '#' and other things indicating space, returns a sequence of
   co-ordinates of all asteroids"
  [input width height] 
  (remove nil? (map #(if (= "#" %1) %2 nil) 
                    input 
                    (vec (for [a (range 0 height)
                               b (range 0 width)]
                           [b a])))))

(defn- calc-direction [[x-ref y-ref] [x y]] 
  [(- x x-ref) (- y y-ref)])

(defn- gcd [[a b]]
  (if
    (zero? b) a
    (recur [b (mod a b)])))

(defn- gcd-on-point [[a b :as point]]
  (let [gcd (Math/abs (gcd point))]
    (if 
      (= 0 a b) [0 0] 
      [(/ a gcd) (/ b gcd) gcd])))

(defn- add-sweep-number [points]
  )

(map #(calc-direction % [3 4]) 
     (asteroid-co-ords (get-input (slurp "resources/inputday10_test.txt")) 5 5))

(def x (->> (asteroid-co-ords (get-input (slurp "resources/inputday10_test.txt")) 5 5)
            (map #(calc-direction % [3 4]))
            div-by-gcd))

(defn- add-pos-in-line* [co-ords]
  (map #(conj %1 %2) 
       (sort-by #(nth % 2) co-ords) 
       (range (count co-ords))))

(defn- add-pos-in-line [co-ords]
  (->> co-ords
       (group-by #(vector (first %) (second %)))
       vals
       (map #(add-pos-in-line* %))
       (apply concat)))

(defn- div-by-gcd [seq-of-points]
  (map #(gcd-on-point %) seq-of-points))

(defn count-visible-asteroids 
  "co-ords is a grid of co-ordinates where there are asteroids"
  [co-ords]
  (for [point co-ords]
    {:point point 
     :count (->> co-ords
                 (map #(calc-direction % point))
                 div-by-gcd
                 (map #(vector (first %) (second %)))
                 set
                 (remove nil?)
                 (remove #(= [0 0] %))
                 count)}))


(def find-max-visible*
  (partial reduce #(if (> (:count %1) (:count %2)) %1 %2)))


(defn find-max-visible [input width height]
  (-> (get-input input)
      (asteroid-co-ords width height)
      count-visible-asteroids
      find-max-visible*))
