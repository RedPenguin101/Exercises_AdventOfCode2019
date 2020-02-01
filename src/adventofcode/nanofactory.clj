(ns adventofcode.nanofactory
  (:require [clojure.string :as s]))

;; ingredient list is just an array [[2 :A] [3 :B]]
;; a receipe is a map {:XSFVQ {:inputs [[11 :BNMWF] [1 :MRVFT] [10 :PBNSF]], :yields 7}}

(defn ingredient-parse [ing-string]
  (let [[amount ingred] (s/split ing-string #" ")]
    [(Integer/parseInt amount) (keyword ingred)]))

(defn recipe-parse [[inputs outputs]]
  (let [[yield output] (ingredient-parse (s/trim outputs))]
    {output {:inputs (vec (map ingredient-parse (s/split (s/trim inputs) #", "))) 
             :yields yield}}))

(defn input-parse [filename]
  (->> (slurp filename)
       s/split-lines
       (map #(s/split % #"=>"))
       (map recipe-parse)))

(comment
  (input-parse "resources/inputday14.txt")
  ;; => ({:XSFVQ {:inputs [[11 :BNMWF] [1 :MRVFT] [10 :PBNSF]], :yields 7}} etc.
  )

(defn required-batches [required yields]
  (if (= (mod required yields) 0)
    (/ required yields)
    (/ (+ required (- yields (mod required yields))) yields)))

(def recipies
  {:A {:yields 1 :inputs [[9 :ORE]]}
   :B {:yields 2 :inputs [[3 :ORE]]}
   :A2 {:yields 1 :inputs [[1 :A] [1 :ORE]]}})

(defn deconstruct [[required ing-name] recipies]
  (if (= ing-name :ORE)
    required
    (let [ingredients (:inputs (ing-name recipies))]
      (* (required-batches required (:yields (ing-name recipies))) (ffirst ingredients)))))

(defn ore-amount [ing recipies]
  (if (empty? ing)
    nil
    (deconstruct ing recipies)))
