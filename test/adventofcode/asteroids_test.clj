(ns adventofcode.asteroids-test
  (:require [adventofcode.asteroids :refer :all]
            [midje.sweet :refer :all]))

(fact
 (find-max-visible (slurp "resources/inputday10_test.txt") 5 5) => {:point [3 4] :count 8}
 (find-max-visible (slurp "resources/inputday10_test2.txt") 10 10) => {:point [5 8] :count 33}
 (find-max-visible (slurp "resources/inputday10_test3.txt") 10 10) => {:point [1 2] :count 35}
 (find-max-visible (slurp "resources/inputday10_test4.txt") 10 10) => {:point [6 3] :count 41}
 (find-max-visible (slurp "resources/inputday10_test5.txt") 20 20) => {:point [11 13] :count 210}

 )