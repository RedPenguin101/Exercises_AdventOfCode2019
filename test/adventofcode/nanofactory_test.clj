(ns adventofcode.nanofactory-test
  (:require [adventofcode.nanofactory :refer :all]
            [midje.sweet :refer :all]))

(def recipies
  [:A {:yields 2 :inputs [[9 :ORE]]}])

(fact
 (ore-amount []) => nil
 )