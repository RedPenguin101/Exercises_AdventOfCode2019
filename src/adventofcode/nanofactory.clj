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

