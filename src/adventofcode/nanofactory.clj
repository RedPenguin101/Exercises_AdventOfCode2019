(ns adventofcode.nanofactory
  (:require [clojure.string :as s]))

(defn ingredient-parse [ing-string]
  (let [[amount ingred] (s/split ing-string #" ")]
    {(keyword ingred) (Integer/parseInt amount)}))

(comment
  (ingredient-parse "5 H")
  ;; => {:H 5}
  )

(defn recipe-parse [[inputs outputs]]
  (let [[output yield] (apply vec (ingredient-parse (s/trim outputs)))]
    {output {:inputs (apply merge (map ingredient-parse (s/split (s/trim inputs) #", "))) 
             :yields yield}}))

(comment
  (recipe-parse ["5 H, 7 B" "6 U"])
  ;; => {:U {:inputs {:H 5, :B 7}, :yields 6}}
  )

(defn input-parse [filename]
  (->> (slurp filename)
       s/split-lines
       (map #(s/split % #"=>"))
       (map recipe-parse)
       (apply merge)))

(comment
  (input-parse "resources/inputday14.txt")
  ;; => {:ZXMGK {:inputs {:WPFP 13}, :yields 6}, :PDCV {:inputs {:PFQRG 4, :XVNL 14}, :yields 5} etc
  )

(defn required-batches [required yields]
  (if (= (mod required yields) 0)
    (/ required yields)
    (/ (+ required (- yields (mod required yields))) yields)))

(defn process [chemical amount recipies]
  (if (= chemical :ORE)
    {:ORE amount}
    (->> (keys (get-in recipies [chemical :inputs]))
         (map #(vector % (* (get-in recipies [chemical :inputs %]) 
                            (required-batches amount (get-in recipies [chemical :yields])))))
         flatten
         (apply hash-map))))

(defn next-level [chemicals recipies]
  (apply merge-with + (map #(process (% 0) (% 1) recipies) (vec chemicals))))

(defn repeat-to-ore [chemicals recipies]
  (if (= [:ORE] (keys chemicals))
    (:ORE chemicals)
    (recur (next-level chemicals recipies) recipies)))
