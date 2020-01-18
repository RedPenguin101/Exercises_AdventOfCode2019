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

(def asts (asteroid-co-ords (get-input (slurp "resources/inputday10_test5.txt")) 20 20))

(fact
 (nth-lasered-from 1 asts [11 13]) => [11 12]
 (nth-lasered-from 2 asts [11 13]) => [12 1]
 (nth-lasered-from 3 asts [11 13]) => [12 2]
 (nth-lasered-from 10 asts [11 13]) => [12 8]
 (nth-lasered-from 20 asts [11 13]) => [16 0]
 (nth-lasered-from 50 asts [11 13]) => [16 9]
 (nth-lasered-from 100 asts [11 13]) => [10 16]
 (nth-lasered-from 199 asts [11 13]) => [9 6]
 (nth-lasered-from 200 asts [11 13]) => [8 2]
 (nth-lasered-from 201 asts [11 13]) => [10 9]
 (nth-lasered-from 299 asts [11 13]) => [11 1])