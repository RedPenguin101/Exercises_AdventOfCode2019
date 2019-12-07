(ns adventofcode.orbit-checksum)

(defn parse-orbit-data [orbit-string]
  (let [split (clojure.string/split orbit-string #"\)")]
    (vector (keyword (split 1)) (keyword (split 0)))))

(defn build-planet-map [planets]
  (reduce #(assoc %1 (%2 0) (%2 1)) {} planets))

(defn find-path-to-COM [name planet-map]
  (cons name (lazy-seq (find-path-to-COM (name planet-map) planet-map))))

(defn path-length [name planet-map]
  (count (take-while #(not= :COM %) (find-path-to-COM name planet-map))))

(defn orbit-checksum [planet-map]
  (reduce + (map #(path-length % planet-map) (keys planet-map))))

(defn load-orbit-file [filename]
  (build-planet-map (map parse-orbit-data (clojure.string/split-lines (slurp filename)))))