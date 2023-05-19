# Matrix Class
Implementation for some matrix computations in C++

- Addition, Subtraction, Multiplication, Inverse
- Transpose, Determinant, Rank, Trace
- Reduced Row Echelon Form using Gaussian Elimination

## Example Run

```bash
$ mkdir build
$ cmake -S . -B build
$ cmake --build build
[ 50%] Building CXX object CMakeFiles/main.dir/main.cpp.o
[100%] Linking CXX executable main
[100%] Built target main
$ build/main
Test client started...
Enter a 3x3 matrix:
1 4 5
7 8 9
4 5 6
Matrix Rank = 3
Matrix Trace = 15
Matrix Determinant = -6
Matrix in RREF:
         1          0          0
         0          1          0
         0          0          1
Matrix Transpose:
         1          7          4
         4          8          5
         5          9          6
Matrix Inverse:
      -0.5  -0.166667   0.666667
         1    2.33333   -4.33333
      -0.5   -1.83333    3.33333
```
