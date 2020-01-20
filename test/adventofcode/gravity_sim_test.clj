(ns adventofcode.gravity-sim-test
  (:require [adventofcode.gravity-sim :refer :all]
            [midje.sweet :refer :all]))

(fact
 "about update position"
 (update-position {:position [0 0 0] :velocity [1 1 1]}) => {:position [1 1 1] :velocity [1 1 1]})

(facts
 "about updating velocity"
 
 (fact
  "when there is only one body, there's no change"
  (update-velocities [{:position [0 0 0] :velocity [0 0 0]}])
  => [{:position [0 0 0] :velocity [0 0 0]}])
 
 (fact 
  "when the the two bodies are in the same place, there is no change"
  (update-velocities [{:position [0 0 0] :velocity [0 0 0]}
                      {:position [0 0 0] :velocity [0 0 0]}]) 
  => [{:position [0 0 0] :velocity [0 0 0]}
      {:position [0 0 0] :velocity [0 0 0]}]
  
  (update-velocities [{:position [0 0 0] :velocity [1 1 1]}
                      {:position [0 0 0] :velocity [-1 -1 -1]}]) 
  
  => [{:position [0 0 0] :velocity [1 1 1]}
      {:position [0 0 0] :velocity [-1 -1 -1]}])

 (fact
  "when two bodies are in different places on the x-axis, their velocities are updated"
  (update-velocities [{:position [0 0 0] :velocity [0 0 0]}
                      {:position [1 0 0] :velocity [0 0 0]}])
  => [{:position [0 0 0] :velocity [1 0 0]}
      {:position [1 0 0] :velocity [-1 0 0]}]
  
  (update-velocities [{:position [0 0 0] :velocity [0 0 0]}
                      {:position [1 0 0] :velocity [0 0 0]}
                      {:position [2 0 0] :velocity [0 0 0]}])
  => [{:position [0 0 0] :velocity [2 0 0]}
      {:position [1 0 0] :velocity [0 0 0]}
      {:position [2 0 0] :velocity [-2 0 0]}])

 (fact
  "when two bodies are in different places on the y-axis, their y velocities are updated"
  (update-velocities [{:position [0 0 0] :velocity [0 0 0]}
                      {:position [0 1 0] :velocity [0 0 0]}])
  => [{:position [0 0 0] :velocity [0 1 0]}
      {:position [0 1 0] :velocity [0 -1 0]}]

  (update-velocities [{:position [0 0 0] :velocity [0 0 0]}
                      {:position [0 1 0] :velocity [0 0 0]}
                      {:position [0 2 0] :velocity [0 0 0]}])
  => [{:position [0 0 0] :velocity [0 2 0]}
      {:position [0 1 0] :velocity [0 0 0]}
      {:position [0 2 0] :velocity [0 -2 0]}])
 
(fact
 "when two bodies are in different places on multiple axes, multiple velocities are updated"
 (update-velocities [{:position [1 0 2] :velocity [0 0 0]}
                     {:position [2 -10 -7] :velocity [0 0 0]}
                     {:position [4 -8 8] :velocity [0 0 0]}
                     {:position [3 5 -1] :velocity [0 0 0]}])
 => [ {:position [1 0 2] :velocity [3 -1 -1]}
     {:position [2 -10 -7] :velocity [1 3 3]}
     {:position [4 -8 8] :velocity [-3 1 -3]}
     {:position [3 5 -1] :velocity [-1 -3 1]}])
 )