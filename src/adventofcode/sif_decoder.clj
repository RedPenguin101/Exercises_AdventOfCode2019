(ns adventofcode.sif-decoder)

(defn split [string]
  (map #(Character/digit % 10) string))

(defn string->layers [string x-size y-size]
  (partition (* x-size y-size) (split string)))

(defn look-through-pixel [x y]
  (if (and (= x 2) (not= y 2)) y x))

(defn compare-layers [layer-1 layer-2]
  (map #(look-through-pixel %1 %2) layer-1 layer-2))

(defn compare-all-layers [[top next & rest]]
  (if ((set top) 2)
    (recur (concat [(compare-layers top next)] rest))
    top))

(defn build-image [code width length]
  (->> (string->layers code width length)
       compare-all-layers
       (partition width)))

(defn img->str [image-data]
  (apply str (map #({0 " " 1 "X"} %) image-data)))

(comment 
  "day8 part 2"
  
  (def input (->  "resources/inputday8.txt"
                  slurp
                  clojure.string/trim))

  (for [line (build-image input 25 6)]
    (img->str line)))
