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

(defn find-common-orbit [name1 name2 planet-map]
  (last (take-while identity (map #(when (= %1 %2) %1)
                              (reverse (take-while #(not= :COM %) (find-path-to-COM name1 planet-map)))
                              (reverse (take-while #(not= :COM %) (find-path-to-COM name2 planet-map)))))))

(defn calculate-jumps [planet1 planet2 planet-map]
  (let [connection (find-common-orbit planet1 planet2 planet-map)
        [dist1 dist2 connect-dist] (map #(path-length % planet-map) [planet1 planet2 connection])]
    (- (+ dist1 dist2) (* 2 connect-dist) 2)))
