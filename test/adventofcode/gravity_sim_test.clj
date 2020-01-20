(ns adventofcode.gravity-sim-test
  (:require [adventofcode.gravity-sim :refer :all]
            [midje.sweet :refer :all]))

(facts
 "about stepping through time"
 (step [{:position [-1 0 2] :velocity [0 0 0]}
        {:position [2 -10 -7] :velocity [0 0 0]}
        {:position [4 -8 8] :velocity [0 0 0]}
        {:position [3 5 -1] :velocity [0 0 0]}])
 => [{:position [2 -1 1] :velocity [3 -1 -1]}
     {:position [3 -7 -4] :velocity [1 3 3]}
     {:position [1 -7 5] :velocity [-3 1 -3]}
     {:position [2 2 0] :velocity [-1 -3 1]}])