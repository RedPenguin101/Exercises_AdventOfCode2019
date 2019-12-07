(ns adventofcode.orbit-checksum-test
  (:require [midje.sweet :refer :all]
            [adventofcode.orbit-checksum :refer :all]))

(facts
  "about orbit notation parser"
  (fact "notation parser can output map of name and orbits"
        (parse-orbit-data "AAA)BBB") => [:BBB :AAA]))

(facts
  "about planet map builder"
  (fact
    (build-planet-map [[:B :A]]) => {:B :A}
    (build-planet-map [[:B :A] [:C :A]]) => {:B :A :C :A}
    ))

(facts
  (fact
    (take-while #(not= % :COM) (find-path-to-COM :A {:A :B :B :C :C :COM})) => [:A :B :C]
    (path-length :A {:A :B :B :C :C :COM :COM :COM}) => 3
    (path-length :L {:B :COM :C :B :D :C :E :D :F :E :G :B :H :G :I :D :J :E :K :J :L :K}) => 7
    (orbit-checksum {:B :COM :C :B :D :C :E :D :F :E :G :B :H :G :I :D :J :E :K :J :L :K}) => 42
    ))
