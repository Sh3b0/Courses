# CIA - Lecture Exercises

> Ahmed Nouralla - a.shaaban@innopolis.university

[TOC]

## Lecture 8 - ABNF

> Define first a BNF then an ABNF grammar for generating these sets of strings:
>
> 1- abc aabbcc aaabbbccc...
>
> 2- abb aabbbb aaabbbbbb aaaabbbbbbbb...
>
> 3- Palindromes: aba, abba, abccba
>
> 4- Valid arithmetic expressions, like 3 + (4 * 2), ((7 - 1) / 2), and 5.
>
> 5- Balanced parentheses, like (), ((())), (()(())), and ((())())

### 1. $a^nb^nc^n$

- It's not possible to use BNF and ABNF to enforce the constraint of having the same `n` across sequences, as it requires a context-sensitive language, while ABNF is used to describe context-free languages as illustrated in Chomsky's hierarchy:

  ![image-20241121233251602](https://devopedia.org/images/article/210/7090.1571152901.jpg)

- Best we can do is match a string of $a^{n_1}b^{n_2}c^{n_3}$ with the following rule

  ```bash
  valid = nA nB nC
  nA = "a" | nA "a"
  nB = "b" | nB "b"
  nC = "c" | nC "c"
  ```

### 2. Strings of increasing "a"s followed by twice as many "b"s

#### BNF

```haskell
<valid> ::= "abb" | "a" <valid> "bb"
```

#### ABNF

```haskell
valid = "abb" / ("a" valid "bb")
```

#### Interpretation:

- "abb" is a valid string
- Generate more valid strings by **perpending "a" and appending "bb"** to a valid string.

### 3. Palindromes

#### BNF

```haskell
<palindrome> ::= <even> | <odd>
<even> ::= "" | <letter> <even> <letter>
<odd> ::= <letter> | <letter> <odd> <letter>
<letter> ::= "a" | "b" | "c" | "d" | ...    ; "a"-"z"
```

#### ABNF

```haskell
palindrome = even / odd
even = "" / (letter even letter)
odd = letter / (letter odd letter)
letter = "a" / "b" / "c" / "d" / ...    ; "a"-"z"
```

#### Interpretation

- A valid palindrome may have even or odd number of letters ('a-z')
- Both have identical definitions. Except that an even palindrome should not have a central letter, while an odd one may have a single arbitrary letter at the enter. 

### 4. Valid arithmetic expressions

#### BNF

```haskell
<expr> ::= <number> | <expr> <operator> <expr> | "(" <expr> ")"
<number> ::= <digit> | <number> <digit>
<digit> ::= "0" | "1" | "2" | "3" | "4" | "5" | "6" | "7" | "8" | "9"
<operator> ::= "+" | "-" | "*" | "/"
```

#### ABNF

```haskell
expr = number / (expr operator expr) / ("(" expr ")")
number = digit / (number digit)
digit = "0" / "1" / "2" / "3" / "4" / "5" / "6" / "7" / "8" / "9"              
operator = "+" / "-" / "*" / "/"
```

#### Interpretation

- A valid arithmetic expression is either
  - A number: one more concatenated `0-9` digits
  - Two expressions with an operator `+ - * /` in between.
- Any valid expression can be surrounded by parenthesis `(` and `)`.

### 5. Balanced parentheses

#### BNF

```haskell
<valid> ::= "" | "(" <valid> ")" | <valid> <valid>
```

#### ABNF

```haskell
valid = "" / ("(" valid ")") / (valid valid)
```

#### Interpretation

- The following generator rules apply to form a valid string of balanced parenthesis 
  - An empty string is considered valid
  - Surrounding a valid string with parenthesis results in a valid string
  - Concatenating two valid strings results in a valid string 
