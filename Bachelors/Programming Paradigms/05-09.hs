-- 5.1
propagate :: (Bool, [Int]) -> [(Bool, Int)]
propagate (_, []) = []
propagate (b, x:xs) =
  (b, x) : propagate (b, xs)

-- 5.2
data Radians = Radians Double deriving Show
data Degrees = Degrees Double deriving Show

toDegrees :: Radians -> Degrees
toDegrees (Radians r) = Degrees (r * 180 / pi)

fromDegrees :: Degrees -> Radians
fromDegrees (Degrees d) = Radians (d * pi / 180)

-- 6.1
type Name = String
data Grade = A | B | C | D
data Student = Student Name Grade

studentsWithA :: [Student] -> [Name]
studentsWithA [] = []
studentsWithA ((Student name A) : xs) = name : (studentsWithA xs)
studentsWithA (_ : xs) = studentsWithA xs

-- 6.2
data Result a
  = Success a
  | Failure String deriving Show

combineResultsWith :: (a -> b -> c) -> Result a -> Result b -> Result c
combineResultsWith f (Success a) (Success b) = (Success (f a b))
combineResultsWith _ (Failure a) _ = (Failure a)
combineResultsWith _ _ (Failure b) = (Failure b)

-- 7.1
-- try print(guess) and you will get an error: no instance for (Show <type>)
guess :: (b0 -> Bool) -> (String -> IO b0) -> IO b0
guess p g = do
  s <- getLine
  x <- g s
  case p x of
    True -> return x
    False -> guess p g

-- 7.2
-- check 07.2.hs

-- 7.3
forIO_ :: [t] -> (t -> IO ()) -> IO ()
forIO_ [] _ = return ()
forIO_ (x:xs) func = do
  func x
  forIO_ xs func

example = do
  forIO_ [1, 2] (\n ->
    forIO_ "ab" (\c ->
      putStrLn (replicate n c)))

-- 8.1
f :: ([a0] -> [a0]) -> a0 -> [a0] -> [a0]
f x y z = x (y : x z)

-- 8.2
-- check 08.2.hs

-- 8.3
type Point = (Int, Int)
data Event = Click | Move Point

toPolyline :: [Event] -> [Point]
toPolyline x = helper x (0, 0)
  where
    helper :: [Event] -> Point -> [Point]
    helper [] _ = []
    helper (e:es) cur =
      case e of
        Click -> cur : helper es cur
        Move (x, y) -> helper es (x, y)

-- 8.4
toPolyline' :: [Event] -> [Point]
toPolyline' x = helper x (0, 0) False
  where
    helper :: [Event] -> Point -> Bool -> [Point]
    helper [] _ _ = []
    helper (e:es) cur justClicked =
      case e of
        Click ->
          if justClicked
            then helper es cur True
            else cur : helper es cur True
        Move (x, y) -> helper es (x, y) False

-- 8.5
prefixes :: [a] -> [[a]]
prefixes [] = [[]]
prefixes (x:xs) = [] : map (\t -> x : t) (prefixes xs)
{-
  1. The anonymus function prepends the head of a list (x) to the given list argument t.
  2. We apply that function to each element t in the list of prefixes of xs.
  3. The function is lazy as it only evaluate (prefixes xs) when needed.
  4. Example:
    prefixes([1..])
    [] : map prepend1 (prefixes [2..])
    [[], map prepend1 [[], (prefixes [2..])]
    [[], [1], prepend1 (prefixes [2..])]
    ...
-}

-- 9.1
k :: (t0 -> String) -> [t0] -> [IO ()]
k g = map (\x -> putStrLn (g x))

-- 9.2
divide :: Int -> Int -> Result Int
divide _ 0 = Failure "division by zero"
divide n m = Success (n `div` m)

splitResults :: [Result a] -> ([String], [a])
splitResults x =
  (
  map
    (\(Failure f) -> f) -- Extract Failure msg
    (filter (\r -> case r of -- Extract Failures
      Failure _ -> True
      Success _ -> False
      ) x) 
  ,
  map
    (\(Success s) -> s) -- Extract Success values
    (filter (\r -> case r of -- Extract Successes
      Failure _ -> False
      Success _ -> True
      ) x)
  )


-- Another solution that accumulates results in two lists with a helper
{-
splitResults :: [Result a] -> ([String], [a])
splitResults x = helper x [] []
  where
    helper :: [Result a] -> [String] -> [a] -> ([String], [a])
    helper [] f s = (reverse f, reverse s)
    helper (Success t:rs) f s = helper rs f (t:s)
    helper (Failure t:rs) f s = helper rs (t:f) s
-}

-- 9.3
data Grid a = Grid [[a]] deriving Show
mapGrid :: (a -> b) -> Grid a -> Grid b
mapGrid f g = helper f g (Grid [])
  where
    helper :: (a -> b) -> Grid a -> Grid b -> Grid b
    helper _ (Grid []) (Grid acc) = Grid (reverse acc)
    helper f (Grid (row:rows)) (Grid acc) =
      helper f (Grid rows) (Grid ((map f row):acc))

-- This turns out to be the smarter way:
-- mapGrid f (Grid g) = Grid (map (map f) g)

-- 9.4
enumerateGrid :: Grid a -> Grid (Int, a)
enumerateGrid g = helper g 1 (Grid []) -- grid, start, result
  where
    helper :: Grid a -> Int -> Grid (Int, a) -> Grid (Int, a)
    helper (Grid []) _ (Grid acc) = Grid (reverse acc)
    helper (Grid (row:rows)) from (Grid acc) =
      helper
        (Grid rows)
        (from + (length row))
        (Grid ((zip [from..] row):acc))



main :: IO ()
main = do
  print(propagate (False, [1, 2, 3]))
  print(propagate (True, [1, 1]))
  print(toDegrees (Radians pi))
  print(fromDegrees (Degrees 180))
  print(studentsWithA [Student "Jack" D, Student "Jane" A])
  print(combineResultsWith (+) (Success 2) (Success 3))
  print(combineResultsWith (+) (Failure "x is undefined") (Failure "crash"))
  example
  print(toPolyline [Move (1, 2), Move (3, 4), Click, Move (5, 6), Click, Click])
  print(toPolyline' [Move (1, 2), Move (3, 4), Click, Move (5, 6), Click, Click])
  print(prefixes "hello")
  print(prefixes [1, 2, 3])
  print(take 4 (prefixes [1..]))
  print(splitResults (zipWith divide [6, 7, 8] [3, 0, 2]))
  print(mapGrid (+1) (Grid [[1,2],[3,4]]))
  print(enumerateGrid (Grid [['a','b'], ['c', 'd']]))
  