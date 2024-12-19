import Data.Char (toUpper)

echo :: IO ()
echo = do
  input <- getLine
  print(map toUpper input)
  echo

main = echo