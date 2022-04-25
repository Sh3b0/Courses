Write a program that takes a file `input.txt` as an input containing a string and returns another file `output.txt` containing one or two lines:

1. The first line is:

   - "YES" if the input is an element of the set A of lambda-terms defined by the following grammar: A ::= V I (A)A I \V.A where V is the set of non-empty strings build from Latin letters and digits;

   - "NO" otherwise. Notice that the 'V character is used instead of 'A'. 

2. If the first line was "NO", then there is no other line. If it was "YES", then the second line is the number of 13-redexes of the input. 

  

## Examples

```
(\x.x)(y)
NO
```

```
(\x.x)y 
YES
1
```

