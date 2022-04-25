import Text.Read

{-
  A program that safely reads an integer
  It returns Nothing if user didn't enter an integer
-}

tryGetInt :: IO (Maybe Int)
tryGetInt = do
  line <- getLine
  return (readMaybe line)

{-
  A program that checks if p x is true
  otherwise it check p (f x)
  then p (f (f (x)) and so on..
  The program returns the first argument for which p was satisfied.
-}

repeatUntil :: (a -> Bool) -> (a -> IO a) -> a -> IO a
repeatUntil p f x =
  if p x
    then return x
    else do 
      tmp <- f x
      repeatUntil p f tmp

 
main = do
  {-
    Waits until input number is less that 10
    Returns Nothing if user didn't enter a number
  -}
  res <- repeatUntil (< Just 10) (\x -> tryGetInt) (Just 10)
  print(res)
  
  -- Returns 5 anyway
  res <- repeatUntil (< 10) (\x -> return 5) 15
  print(res)
