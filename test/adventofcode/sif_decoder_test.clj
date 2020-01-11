(ns adventofcode.sif-decoder-test
  (:require [adventofcode.sif-decoder :refer :all]
            [midje.sweet :refer :all]))

; strategy: take top layer, check if white or black, if not go to next layer

(fact 
 "returns the layer when there's only one layer"
 (build-image "0000" 2 2) => [[0 0] [0 0]])

(fact
 "returns the top layer when the top layer is all black"
 (build-image "00000000" 2 2) => [[0 0] [0 0]])

(fact
 (build-image "0222112222120000" 2 2) => [[0 1] [1 0]])
