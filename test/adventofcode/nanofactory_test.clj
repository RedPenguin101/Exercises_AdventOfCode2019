(ns adventofcode.nanofactory-test
  (:require [adventofcode.nanofactory :refer :all]
            [midje.sweet :refer :all]))

(def test-recipies
  {:A {:yields 1 :inputs [[9 :ORE]]}
   :B {:yields 2 :inputs [[3 :ORE]]}
   :A2 {:yields 1 :inputs [[1 :A] [1 :ORE]]}
   :B2 {:yields 2 :inputs [[2 :B] [2 :ORE]]}
   :AB {:yields 3 :inputs [[3 :A] [4 :B]]}})

(fact
 ;(ore-amount [] test-recipies) => nil
 (ore-amount2 [1 :ORE] test-recipies) => 1
 (ore-amount2 [5 :ORE] test-recipies) => 5
 (ore-amount2 [2 :A] test-recipies) => 18
 (ore-amount [3 :A] test-recipies) => 27
 (ore-amount [1 :B] test-recipies) => 3
 (ore-amount [2 :B] test-recipies) => 3
 (ore-amount [1 :A2] test-recipies) => 10
 (ore-amount [2 :B2] test-recipies) => 5
 (ore-amount [2 :B2] test-recipies) => 5
 (ore-amount [3 :B2] test-recipies) => 10
 (ore-amount [3 :AB] test-recipies) => 33
 )


(def test-recipies2
  {:A {:yields 2 :inputs [[9 :ORE]]}
   :B {:yields 3 :inputs [[8 :ORE]]}
   :C {:yields 5 :inputs [[7 :ORE]]}
   :AB {:yields 1 :inputs [[3 :A] [4 :B]]}
   :BC {:yields 1 :inputs [[5 :B] [7 :C]]}
   :CA {:yields 1 :inputs [[4 :C] [1 :A]]}
   :FUEL {:yields 1 :inputs [[2 :AB] [3 :BC] [4 :CA]]}
   })

(future-fact
 (ore-amount [10 :A] test-recipies2) => 45
 (ore-amount [24 :B] test-recipies2) => 64
 (ore-amount [1 :FUEL] test-recipies2) => 165
 )