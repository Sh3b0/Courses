# Task 5 - Userspace process

## Task

- Write a program (in any language) that reports (in any format) all executable binaries in a specified directory and the libraries they use.

- Application should separate output for arches x86, x86-64, arm, and aarch64.
- Output must be sorted from the most used libraries to the least used libraries.

## Tests:

- Sample test:

  ```bash
  git clone https://github.com/JonathanSalwan/binary-samples
  cd binary-samples
  sudo chmod +x ../program.sh
  ../program.sh . > ../output.txt # check output.txt for results
  ```
  
  

