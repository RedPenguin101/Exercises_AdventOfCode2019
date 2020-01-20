(ns adventofcode.gravity-sim-test
  (:require [adventofcode.gravity-sim :refer :all]
            [midje.sweet :refer :all]))

(fact
 "about update position"
 (update-position {:position [0 0 0] :velocity [1 1 1]}) => {:position [1 1 1] :velocity [1 1 1]})

(fact)