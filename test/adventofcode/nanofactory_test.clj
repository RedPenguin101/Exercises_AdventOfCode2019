(ns adventofcode.nanofactory-test
  (:require [adventofcode.nanofactory :refer :all]
            [midje.sweet :refer :all]))

(def recipies
  {:A {:yields 1 :inputs [[9 :ORE]]}
   :B {:yields 2 :inputs [[3 :ORE]]}})

(fact
 (ore-amount [] recipies) => nil
 (ore-amount [1 :ORE] recipies) => 1
 (ore-amount [5 :ORE] recipies) => 5
 (ore-amount [2 :A] recipies) => 18
 (ore-amount [3 :A] recipies) => 27
 (ore-amount [1 :B] recipies) => 3
 (ore-amount [2 :B] recipies) => 3
 )
