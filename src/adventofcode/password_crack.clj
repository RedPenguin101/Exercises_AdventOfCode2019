(ns adventofcode.password-crack)

(defn valid-code? [code]
  (let [code-str (str code)]
    (and
      (= 6 (count code-str))
      (some #(= (count %) 2) (partition-by identity (seq code-str)))
      (apply <= (map (comp #(Integer/parseInt %) str) (seq code-str)))))):w