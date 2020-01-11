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

## Day 8
This was a pretty simple one.

### Part 2
The problem is to take an encoded message consisting of 0s (black pixels) 1s (white pixels) and 2s (transparant pixels), decode it into image 'layers', then put the layers on top of eachother so a final image comes through.

My algorithm for this was
1. deconstruct the image into layers consisting of vectors of `ints`
2. look at the top layer. If there are no 2s (transparencies) in it, return the top layer
3. otherwise take the top two layers and 'merge' them (return a new vector where the value of a position is from bottom layer if the top layer has 2 in that position, and from the top layer if not)
4. recur with the result of 3 in place of the top two layers
5. partition the resulting vector into 2 dimensions, and print

My code was pretty verbose, at 23 SLOC and 7 functions. Important bits:

```clj
(defn look-through-pixel [x y]
  (if (and (= x 2) (not= y 2)) y x))

(defn compare-layers [layer-1 layer-2]
  (map #(look-through-pixel %1 %2) layer-1 layer-2))

(defn compare-all-layers [[top next & rest]]
  (if ((set top) 2)
    (recur (concat [(compare-layers top next)] rest))
    top))

(defn build-image [code width length]
  (->> (string->layers code width length)
       compare-all-layers
       (partition width)))
```

Most other solutions were similar in approach, but much more concise. The cleanest I saw just used reduce to compact nearly everything into a single LOC:

```clj
(reduce (fn [under over] (map #(if (= %2 \2) %1 %2) under over)) layers)
```

It does have a nested anonymous function, but at the least I would replace my `compare-all-layers` function with a reduce, since it's basically a reimplementation. the `(if pred x (recur (concat (function x y) rest)))` (i.e. take a sequence and process element against a result) is a pattern I should look out for in future as something that should be refactored into a `reduce`.

(note it works on layers from bottom-to-top)

The other approach I saw was to not operate on layers, but on a single pixel - i.e. take a position in the vector and drill down until you find a non-transparent one:

```clj
(defn find-pred [pred x]
  (some #(if (pred %) % nil) x))

(defn first-not-transparent [& pixels]
  (find-pred #(not= 2 %) pixels))

(defn part-2 []
  (->> layers
       (apply map first-not-transparent)
       (partition width)
       pprint/pprint))
```
