(ns adventofcode.gravity-sim-test
  (:require [adventofcode.gravity-sim :refer :all]
            [midje.sweet :refer :all]))

(def start2 [{:position [-1 0 2] :velocity [0 0 0]}
            {:position [2 -10 -7] :velocity [0 0 0]}
            {:position [4 -8 8] :velocity [0 0 0]}
            {:position [3 5 -1] :velocity [0 0 0]}])

(facts 
 "about stepping through time"
 (step start2) => [{:position [2 -1 1] :velocity [3 -1 -1]}
                  {:position [3 -7 -4] :velocity [1 3 3]}
                  {:position [1 -7 5] :velocity [-3 1 -3]}
                  {:position [2 2 0] :velocity [-1 -3 1]}]
 
 (steps start2 10) => [{:position [2 1 -3], :velocity [-3 -2 1]}
                       {:position [1 -8 0], :velocity [-1 1 3]}
                       {:position [3 -6 1], :velocity [3 2 -3]}
                       {:position [2 0 4], :velocity [1 -1 -1]}])

(facts
 "about energy calculation"
 (total-energy {:position [2 1 -3] :velocity [-3 2 1]}) => 36
 (reduce + (map total-energy [{:position [2 1 -3], :velocity [-3 -2 1]}
                              {:position [1 -8 0], :velocity [-1 1 3]}
                              {:position [3 -6 1], :velocity [3 2 -3]}
                              {:position [2 0 4], :velocity [1 -1 -1]}])) => 179)

(fact 
 "about slurping input"
 (slurp-to-bodies "resources/inputday12.txt") 
 => [{:position [6 10 10] :velocity [0 0 0]}
     {:position [-9 3 17] :velocity [0 0 0]}
     {:position [9 -4 14] :velocity [0 0 0]}
     {:position [4 14 4] :velocity [0 0 0]}])
