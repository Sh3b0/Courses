# Programming Paradigms Tests

## Test 1

1. Using only bare λ-calculus (variables, λ- abstraction and application), write down a λ-term for logical or (exclusive OR) of two Church Booleans. Remember that with Church Booleans we have the following encoding:

   ```
   tru = λt.λf.t
   fls = λt.λf.f
   ```

2. Verify your implementation of or by writing down evaluation sequence for the term `xor tru tru`.

3. Using only bare λ-calculus (variables, λ-abstraction and application), write down a. A-term for a. function `doubleThenInc` that computes 2n + 1 for any given Church numeral n. Remember that with Church numerals we have the following encoding:

   ```
   c0 = λs.λz.z
   c1 = λs.λz.sz
   c2 = λs.λz.s(sz)
   c3 = λs.λz.s(s(sz))
   ```

   <div style="page-break-after: always; break-after: page;"></div>

## Test 2

1. Using explicit recursion implement in Racket a function alternating-sum that computes a sum of a list of numbers, multiplying every second number by -1.
   - For example, `(alternating-sum (list 6 2 4 1 3 9))` should compute `6 - 2 + 4 - 1 + 3 - 9 = 1`.

2. Consider the following definitions in Racket:

   ```python
   (define (dec n) (- n 1))
   
   (define (f n)
   	(cond
           [(<= n 2) (- 3 n)]
           [else (+ (f (dec n)) (f (dec (dec n))))]))
   ```

   Using the Substitution Model explain step-by-step how the following expression is computed (you can evaluate cond-expressions immediately, but evaluation of function calls to **f** and **dec** have to be explicit)

   ```swift
   (f 3)
   ```

   <div style="page-break-after: always; break-after: page;"></div>

## Test 3

Consider the following sample definition:

```lisp
(define employees

    '(("John" "Malkovich" . 29)

    ("Anna" "Petrova" . 22)

    ("Ivan" "Ivanov" . 23)

    ("Anna" "Karenina" . 40)))
```

Recall that `'("Anna" "Petrova"  22)` is equivalent to `(cons "Anna" (cons "Petrova" 22))`

This value is a pair where first element is the first name of an employee and second element is a pair of last name and age.

1. Implement a function **fullname** that takes employee and returns their full name as a pair of first and last name:

   ```lisp
   (fullname ("John" "Malkovich" . 29))
   
   ; ("John" . "Malkovich")
   ```

2. Using higher-order functions (`map`, `ormap`, `andmap`, `filter`, `foldl`) and without explicit recursion, write down an expression that computes a list of entries from employees where employee's first name is "Anna".

3. Using higher-order functions (map, ormap, andmap, filter, fold1) and without explicit recursion, implement a function employees-over-25 that computes a list of full names of employees whose age is greater than 25 given a list of employee entries as input:

   ```lisp
   (employees-over-25 employees)
   
   ; ( ("John," "frialkovich") ("Anna" . "Karenina"))
   ```

   <div style="page-break-after: always; break-after: page;"></div>

## Test 4 

1. Using explicit recursion, implement a function replicate that takes a number n and a value x, and returns a list with n copies of x:

   ```lisp
   (replicate 10 'a)
   ; '(a a a a a a a a a a)
   
   (replicate 3 '(1 . 2))
   
   ; '((1 . 2) (1 . 2) (1 . 2))
   ```

2. Using explicit recursion, implement a function encode that groups consecutive duplicate elements into pairs of the representative element and the length of the group:

   ```lisp
   (encode '(a a b c c c a a a))
   
   ; ((a . 2) (b . 1) (c . 3) (a . 3))
   ```

​	Hint: first implement prepend function that adds one more symbol at the beginning of an already encoded sequence.

3. Using higher-order functions (map, ormap, andmap, filter, fold) and without explicit recursion, implement a function **decode** that is the inverse of **encode:**

   ```lisp
   (decode '((a . 2) (b . 1) (c . 3) (a . 3)))
   
   ; '(a a b c c c a a a)
   ```

   <div style="page-break-after: always; break-after: page;"></div>

## Test 5

1. Using explicit recursion, implement function`propagate :: (Bool, [Int] ) -> [(Bool, Int)]` that pairs given boolean with every integer in a given list:

   ```haskell
   example1 = propagate (False, [1, 2, 3])
   
   -- [(False,1), (False,2), (False,3)]
   
   example2 = propagate (True, [1, 1])
   
   -- [(True,1), (True,1)]
   ```

2. Consider the following definitions:

   ```haskell
   data Radians = Radians Double
   data Degrees = Degrees Double
   pi :: Double
   pi = 3.14159
   ```

   - Implement the following functions that convert between degrees and radians:

     ```haskell
     toDegrees :: Radians -> Degrees
     fromDegrees :: Degrees -> Radians
     ```

     <div style="page-break-after: always; break-after: page;"></div>

## Test 6

Consider the following definitions:

```haskell
type Name = String
data Grade = A | B | C | D
data Student = Student Name Grade
data Result a
	= Success a
	| Failure String
```

1. Using explicit recursion, implement function `studentsWithA :: [Student] -> [Name]` that re­turns a list of names of students with A grade:

   ```haskell
   example1 = studensWithA [Student "Jack" D, Student "Jane" A]
   -- ["Jane"]
   ```

   Note: you cannot use (==) to compare grades for equality.

2. Implement a polymorphic higher-order function

   ```haskell
   combineResultsWith :: (a -> b -> c) -> Result a -> Result b -> Result c
   ```

   that applies a function to two results if both are present and returns the first available error message otherwise:

   ```haskell
   example2 = combineResultsWith (+) (Success 2) (Success 3)
   -- Success 5
   
   example3 = combineResultsWith (+) (Failure "x is undefined") (Failure "crash")
   -- Failure "x is undefined"
   ```

<div style="page-break-after: always; break-after: page;"></div>

## Test 7

1. What is the type of guess in the following program?

   ```haskell
   guess p g = do
   	s <- getLine
   	x <- g s
   	case p x of
   		True -> return x
   		False -> guess p g
   ```

2. Implement a program `echo :: IO ()` that goes through an infinite loop of reading user input and printing it back in CAPS. Use `toUpper :: Char -> Char` to convert a single character to upper case. Remember that `type String = [Char]`.

   ```haskell
   import Data.Char (toUpper)
   echo :: IO ()
   ```

3. Implement a polymorphic function `forIO_` that runs a program for each element in a given list (using given function):

   ```haskell
   example = do
   	forIO_ [1, 2] (\n ->
   		forIO_ "ab" (\c ->
   			putStrLn (replicate n c)))
   
   -- When executed, example prints out:
   -- a
   -- aa
   -- b
   -- bb
   ```

   <div style="page-break-after: always; break-after: page;"></div>

## Test 8

1. What is the type of f in the following function?

   ```haskell
   f x y z = x (y : x z)
   ```

2. Implement a polymorphic function `repeatUntil :: (a -> Bool) -> (a -> IO a) -> a -> IO a.`

   Expression `repeatUntil p f x` should repeat application of f to x until predicate p is satisfied. Example usage:

   ```haskell
   tryGetInt :: IO (Maybe Int)
   tryGetInt = do
   	line <- getLine
   	return (readMaybe line)
   example1 = repeatUntil (< Just 10) (\x -> tryGetInt) Nothing
   -- asks user for input until the user provides a number less than 10
   example2 = repeatUntil (< 10) (\x -> return 5) 15
   -- returns 5
   ```

3. Consider the following data type, corresponding to possible input events:

   ```haskell
   type Point = (Int, Int)
   data Event = Click | Move Point
   ```

   Implement function `toPolyline :: [Event] -> [Point]` that returns a list of points where the user has clicked.

   ```haskell
   example3 = toPolyline [Move (1, 2), Move (3, 4), Click, Move (5, 6), Click, Click]
   -- [(3, 4), (5, 6), (5, 6)]
   ```

4. Implement function `toPolyline' :: [Event] -> [Point]` that returns a list of points where the user has clicked, without duplicate consecutive clicks:

   ```haskell
   example3 = toPolyline [Move (1, 2), Move (3, 4), Click, Move (5, 6), Click, Click]
   -- [(3, 4), (5, 6)]
   ```

5. Implement function `prefixes :: [a] -> [[a]]` that returns a list of all prefixes of the given list. The function should be lazy and support infinite lists:

   ```haskell
   example5 = prefixes "hello"
   -- ["","h","he","hel","hell","hello"]
   example6 = prefixes [1, 2, 3]
   -- [[], [1], [1,2], [1,2,3]] 
   example7 = take 4 (prefixes [1..])
   -- [[], [1], [1,2], [1,2,3]]
   ```

<div style="page-break-after: always; break-after: page;"></div>

## Test 9

1. What is the type of **k** in the following declaration?

   ```haskell
   k g = map (\x -> putStrLn (g x))
   ```

2. Given the following definition of **Result,** write down a polymorphic function`splitResults :: [Result a] -> ([String], [a])` that splits results into Failures and Successes

   ```haskell
   data Result a = Success a | Failure String
   divide :: Int -> Int -> Result Int
   divide _ 0 = Failure "division by zero"
   divide n m = Success (n `div` m)
   
   example2 = splitResults (zipWith divide [6, 7, 8] [3, 0, 2])
   -- (["division by zero"], [2,4])
   ```

3. Consider the following data. type, representing a 2D grid: `data Grid a = Grid [[a]]` Implement function `mapGrid :: (a -> b) -> Grid a -> Grid b` that applies a given function to every element in the grid.

4. Implement function `enumerateGrid :: Grid a -> Grid (Int, a)` that enumerates all elements in a, given grid, line by line, from top to bottom and from left. to right:

   ```haskell
   example4 = enumerateGrid (Grid [['a','b'], ['c', 'd']]
   -- Grid [[(1,'a'), (2,'b')], [(3,'c'), (4,'d')]]
   ```

   <div style="page-break-after: always; break-after: page;"></div>

## Test 10

Consider the following knowledge base:

```swift
% student (Name, Group)
student(alisa, 2).
student(bob,  1).
student(chloe, 2).
student(denise, 1).
student(edward, 2).

friend(alisa, bob).
friend(alisa, denise).
friend(bob, chloe).
friend(bob, edward).
friend(chloe, denise).
friend(denise, edward).

unary(z).
unary(s(X)) :- unary(X).
```



1. Write down predicate groupmates/2 that checks whether two students are from the same group.

   ```swift
   ?- groupmates(alisa, bob).
   false
   
   ?- groupmates(alisa, edward).
   true
   ```

2. Draw the search tree for the following query

   ```swift
   ?- friend(alisa, Y), friend(Y, Z).
   ```

3. Implement a multiplication of unary numbers as a predicate `mult/2`:

   ```swift
   ?- mult(s(s(z)), s(s(s(z))), X).
   X = s(s(s(s(s(s(z))))))
   
   ?- mult(X, s(s(s(z))), s(s(s(s(s(s(z))))))).
   X = s(s(z))
   ```

<div style="page-break-after: always; break-after: page;"></div>

## Test 11

1. Write down predicate `sublist/2` that checks whether the first list is a sublist of the second one.

   ```swift
   ?- sublist([2, 3, 5] , [1, 2, 3, 4, 5]).
   true
   ?- sublist(X, [1, 2]).
   X = [1, 2]
   X = [1]
   X = [2]
   X = []
   ```

2. Implement predicate `search/3` that checks whether the first list occurs in the second list at a given position:

   ```swift
   ?- search([a,b,a], [c,a,b,a,b,a,d], Pos).
   Pos = 1
   Pos = 3
   
   ?- search(Needle, [c,a,b,a,b,a,d], 5).
   Needle = []
   Needle = [a]
   Needle = [a, d]
   ```

3. Implement predicate `replace/4`. `replace (Old, New, OldWhole, NewWhole)`checks whether NewWhole can be constructed from OldWhole by replacing some occurrences of Old with New:

   ```swift
   ?- replace([a,b], [x,y,z], [a,b,r,a,b,a], L).
   L = [x, y, z, r, x, y, z, a]
   L = [x, y, z, r, a, b, al
   L = [a, b, r, x, y, z, a]
   L = [a, b, r, a, b, a]
   
   ?- replace([a,a], [x,y], [a,a,a,a], L).
   L = [x, y, x, y]
   L = [x, y, a, a]
   L = [a, x, y, a]
   L = [a, a, x, y]
   L = [a, a, a, a]
   ```

   <div style="page-break-after: always; break-after: page;"></div>

## Test 12

1. Write down predicate `minimum/2` that finds the minimum number in a list of numbers. *Hint: use a helper function.*

   ```swift
   ?- minimum([3, 6, 2, 5, 4, 7], X).
   X = 2
   ```

2. Without using negation or fail, implement predicate `remove/3` that removes all occurrences of a given element from a list:

   ```swift
   ?- remove(e, [a,p,p,l,e,p,i,e], X).
   X = [a, p, p, l, p, i]
   ```

3. Implement predicate `removeU/3` that removes all elements from a list that can be unified with a given term:

   ```swift
   ?- removeU(t(X), [1, a, A, tb, t(b), tX, t(B)], Y).
   Y = [1, a, tb, tX]
   ```

4. Consider predicates `nat/1` and `nat/2` defined as follows:

   ```swift
   nat(0).
   nat(N) :- nat(K), N is K+1.
   
   nat(0, 0) :- !.
   nat(0, Max) :- Max > 0.
   nat(N, Max) :- M is Max-1, nat(K, M), N is K+1.
   ```

   Implement predicate `prime/1` that checks if a given number is a prime number. The implemen­tation should encode the following definition: "a number *N* is prime if there there does not exist a pair of numbers 1 < *X,* Y < *N* such that *X* * Y = *N."*

   ```swift
   ?- prime (29).
   true
   
   ?- prime(N).
   N = 2 
   N = 3 
   N = 5
   ```

5. Implement predicate `allColors/1` that is true whenever a given list consists of all distinct colors defined by predicate color: `color(red). color(green). color(blue). ` Sample queries:

   ```swift
   ?- allColors([red, green, blue]).
   true
   
   ?- allColors([red, G, blue]).
   G = green
   
   ?- allColors(Colors).
   Colors = [blue, green, red]
   ```

   *Hint: a list of all colors is a list of distinct colors such that there does not exist* a *color that is* *not in that list.*

