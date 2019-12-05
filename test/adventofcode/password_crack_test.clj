(ns adventofcode.password-crack-test
  (:require [midje.sweet :refer :all]
            [adventofcode.password-crack :refer :all]))

(facts
  "about valid passwords"
  (fact "A valid password is a 6 digit number"
        (valid-code? 122456) => true
        (valid-code? 12345) => false
        (valid-code? 1234567) => false)
  (fact "Two adjacent numbers are the same (only 2 for part 2)"
        (valid-code? 133456) => true
        (valid-code? 133356) => nil
        (valid-code? 123456) => nil)
  (fact "The code only increases number-to-number"
        (valid-code? 122345) => true
        (valid-code? 556789) => true
        (valid-code? 554678) => false))