#include "../errors.h"
#include "../log_manager.h"
#include <unordered_map>
#include <iomanip>

#ifndef MATRIX_H
#define MATRIX_H


namespace std /// To support hashing indices
{
    template<>
    struct hash<pair<int, int> > {
        size_t operator()(const pair<int, int> &k) const {
            return ((hash<int>()(k.first) ^ (hash<int>()(k.second) << 1u)) >> 1u);
        }
    };
}

template <typename T>
class matrix{

private:

    int r, c, rank = -1;
    std::unordered_map<std::pair<int, int>, T> m; // using a hashMap for performance.
    T zero, one, error, trace, determinant;
    matrix<T> *rref, *inverse;
    bool rref_called = false;

public:

    /// For debugging; prints a given matrix argument along with a message.
    void debug(matrix<T> &t, const std::string& msg){
        std::cout << msg << "\n";
        for (int i = 0; i < t.r; i++) {
            for (int j = 0; j < t.c; j++)
                std::cout << t.get(i, j) << " ";
            std::cout << '\n';
        }
        std::cout << "-------------------\n";
    }

    /// ctor; overload _0 and _1 variables for matrices of user defined typed (fraction, complex, etc..)
    matrix<T>(int row, int col, T _0 = 0, T _1 = 1, T err = 0.01){
        lg << "**A matrix of dimension " << row << "x" << col << " is created**\n";
        r = row;
        c = col;
        zero = _0;
        one = _1;
        error = err;
        determinant = one;
    }

    /// Class member functions; are implemented in matrix.tpp file to provide abstraction.
    void set(int row, int col, T value);
    T get(int row, int col);
    matrix<T> getIdentity(int n);
    matrix<T> getInverse();
    T getDeterminant();
    matrix<T> getTranspose();
    int getRank();
    T getTrace();
    matrix<T> operator+(matrix<T> &t);
    matrix<T> operator-(matrix<T> &t);
    matrix<T> operator*(matrix<T> &t);
    matrix<T> operator/(matrix<T> &t);
    matrix<T> getRREF();

    /// Overloading input operator
    friend std::istream &operator>>(std::istream &input, matrix<T> &t) {
        for (int i = 0; i < t.r; i++) {
            for (int j = 0; j < t.c; j++) {
                input >> t.m[{i, j}];
            }
        }
        return input;
    }

    /// Overloading output operator
    friend std::ostream &operator<<(std::ostream &output, matrix<T> &t) {
        for (int i = 0; i < t.r; i++) {
            for (int j = 0; j < t.c; j++) {
                if (std::abs(t.m[{i, j}]) < t.error) t.m[{i, j}] = t.zero;
                output << std::setw(10) << t.m[{i, j}];
                (j == t.c - 1) ? output << '\n' : output << ' ';
            }
        }
        return output;
    }
};

/// Contains implementation details.
#include "matrix.tpp"

#endif //MATRIX_H
