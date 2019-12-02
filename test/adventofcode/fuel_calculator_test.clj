(ns adventofcode.fuel-calculator-test
  (:require [midje.sweet :refer :all]
            [adventofcode.fuel-calculator :refer :all]))

(facts "about calculating fuel requirements for a module"
       (fact "fuel requirement for a zero mass module is zero"
             (fuel-required 0) => 0)
       (fact "fuel requirement for a 1 mass module is 0"
             (fuel-required 1) => 0)
       (fact "for larger individual modules"
             (fuel-required 12) => 2
             (fuel-required 14) => 2
             (fuel-required 1969) => 966
             (fuel-required 100756) => 50346)
       (fact "for multiple modules"
             (total-fuel-required [0]) => 0
             (total-fuel-required [12]) => 2
             (total-fuel-required [12 14]) => 4
             (total-fuel-required [12 14 1969 100756]) => 51316))
