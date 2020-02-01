(ns adventofcode.nanofactory-test
  (:require [adventofcode.nanofactory :refer :all]
            [midje.sweet :refer :all]))

(def recipies
  {:A {:yields 1 :inputs [[9 :ORE]]}})

(fact
 (ore-amount [] recipies) => nil
 (ore-amount [5 :ORE] recipies) => 5
 (ore-amount [2 :A] recipies) => 18
 (ore-amount [3 :A] recipies) => 27
 )
