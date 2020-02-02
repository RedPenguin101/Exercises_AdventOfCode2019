(ns adventofcode.nanofactory-test
  (:require [adventofcode.nanofactory :refer :all]
            [midje.sweet :refer :all]))

(def test-recipies2
  {:A {:yields 2 :inputs {:ORE 9}}
   :B {:yields 3 :inputs {:ORE 8}}
   :C {:yields 5 :inputs {:ORE 7}}
   :AB {:yields 1 :inputs {:A 3 :B 4}}
   :BC {:yields 1 :inputs  {:B 5 :C 7}}
   :CA {:yields 1 :inputs {:C 4 :A 1}}
   :FUEL {:yields 1 :inputs {:AB 2 :BC 3 :CA 4}}
   })

(facts
 (process :A 2 test-recipies2) => {:ORE 9}
 (process :A 4 test-recipies2) => {:ORE 18}
 (process :A 1 test-recipies2) => {:ORE 9}
 (process :B 2 test-recipies2) => {:ORE 8}
 (process :B 3 test-recipies2) => {:ORE 8}
 (process :B 4 test-recipies2) => {:ORE 16}
 (process :AB 1 test-recipies2) => {:A 3 :B 4}
 (process :FUEL 1 test-recipies2) => {:AB 2 :BC 3 :CA 4}
 (process :ORE 100 test-recipies2) => {:ORE 100}
 )

(facts
 (next-level {:A 2} test-recipies2) => {:ORE 9}
 (next-level {:A 2 :B 3} test-recipies2) => {:ORE 17}
 (next-level {:AB 1} test-recipies2) => {:A 3 :B 4}
 (next-level {:ORE 100} test-recipies2) => {:ORE 100}
 )

(facts
 (repeat-to-ore {:FUEL 1} test-recipies2) => 165
 ;(repeat-to-ore {:FUEL 1} (input-parse "resources/testday14.txt")) => 165
 )
