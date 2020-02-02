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
  {:A {:yields 2 :inputs {:ORE 9}}
   :B {:yields 3 :inputs {:ORE 8}}
   :C {:yields 5 :inputs {:ORE 7}}
   :AB {:yields 1 :inputs {:A 3 :B 4}}
   :BC {:yields 1 :inputs  {:B 5 :C 7}}
   :CA {:yields 1 :inputs {:C 4 :A 1}}
   :FUEL {:yields 1 :inputs {:AB 2 :BC 3 :CA 4}}})


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
