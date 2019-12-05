# Advent Of Code 2019: A digest of solutions in Clojure 
https://adventofcode.com/

This is a digest of the problems of AoC 2019, and notes on how they were solved by members of /r/clojure.

## Day 1
_pending_

## Day 2
_pending_

## Day 3

## Day 4: Secure Container

The challenge here was essentially about checking whether the digits of a numeric code meet certain criteria, namely
* There are two adjacent digits in the code that are the same (at least two in part 1, exactly two in part 2)
* The digits never descend when read left to right

The most common approach I saw was to to use an `and`, with one statement for each condition (strictly there were a couple more conditions, but these were not interesting), though how each was checked actually varied quite a lot.

_ceronman's_ solution was an example of this, in this case using regex to check the repeated digit. The descending ordering was achieved in these methods by deconstructing the code into a sequence of numbers and then `apply`ing `<=` to it, so it returned true only if they were ascending.

```clj
(defn possible-password? [value]
  (and
   (apply <= (map #(Character/digit % 10) (str value)))
   (some? (re-find #"(\d)\1" (str value)))))
```

The approach of using `(apply <=` was also common (and very elegant), though there were a few ways of desconstructing the number. _ceronman_ used `Character/digit % 10` and _rmfbarker_ turned it into a sequence of characters and then parsed the sequence back to integers.

```clj
(defn valid-password? [pwd]
  (and
    (= (count (str pwd)) 6)
    (< 402328 pwd 864247)
    (some #(= 2 (count %)) (partition-by identity (seq (str pwd))))
    (apply <= (map (comp #(Integer/parseInt %) str) (seq (str pwd))))))
```

_rmfbarker_ also used a different method of finding repeat characters: again desconstrucing the code into a sequence, then using `partition-by` to group the same digits together. By calculating the length of each group you can check whether there are any repeating digits (note that this takes advantage of the fact that because a valid code is in ascending order, any two digits that appear twice in a code will by definition be next to eachother).

While the `partition-by` method is more complicated than the regex, it does have the advantage that you can move from an 'at least two' repeat digits to an 'exactly two' by simply changing the comparator in the predicate function, while the regex change requires a bit more work (though not much).

There were a couple of variations on using and: _agrison_ and ? used `every-pred`, and built and passed in predicates. 