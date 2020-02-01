(ns adventofcode.nanofactory-test
  (:require [adventofcode.nanofactory :refer :all]
            [midje.sweet :refer :all]))

(def test-recipies
  {:A {:yields 1 :inputs [[9 :ORE]]}
   :B {:yields 2 :inputs [[3 :ORE]]}
   :A2 {:yields 1 :inputs [[1 :A] [1 :ORE]]}})

(fact
 (ore-amount [] test-recipies) => nil
 (ore-amount [1 :ORE] test-recipies) => 1
 (ore-amount [5 :ORE] test-recipies) => 5
 (ore-amount [2 :A] test-recipies) => 18
 (ore-amount [3 :A] test-recipies) => 27
 (ore-amount [1 :B] test-recipies) => 3
 (ore-amount [2 :B] test-recipies) => 3
 ;(ore-amount [1 :A2] test-recipies) => 10
 )
