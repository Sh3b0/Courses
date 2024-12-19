/// Contains template definitions of signatures declared in matrix.h

#include "matrix.h"

template<typename T>
void matrix<T>::set(int row, int col, T value) {
    m[{row, col}] = value;
}

template<typename T>
T matrix<T>::get(int row, int col) {
    return m[{row, col}];
}

template<typename T>
matrix<T> matrix<T>::getIdentity(int n) {
    lg << "--Calculating identity matrix of dimension " << n << "x" << n << '\n';
    matrix result(n, n);
    for (int i = 0; i < n; i++) {
        for (int j = 0; j < n; j++) {
            if (i == j) result.set(i, j, one);
            else result.set(i, j, zero);
        }
    }
    lg << result;
    return result;
}

template<typename T>
matrix<T> matrix<T>::getInverse() {
    lg << "--Calculating inverse matrix for:\n" << *this << '\n';
    if (r != c) {
        throw DimensionError("Unable to calculate Inverse; Matrix is not square");
    }
    else if (getRank() < r) {
        lg << "ERROR: Matrix has rank = " << rank << " which is < " << r << ", therefore, it's not invertible\n";
        throw MathError("Unable to calculate Inverse; Matrix is singular");
    }
    return *inverse;
}

template<typename T>
T matrix<T>::getDeterminant() {
    lg << "--Calculating determinant for:\n" << *this << '\n';
    if (r != c) {
        throw DimensionError("Unable to calculate determinant; Matrix is not square");
    }
    if (!rref_called) getRREF();
    return determinant;
}

template<typename T>
matrix<T> matrix<T>::getTranspose() {
    lg << "--Calculating transpose for:\n" << *this << '\n';
    matrix result(c, r);
    for (int i = 0; i < c; i++)
        for (int j = 0; j < r; j++)
            result.set(i, j, get(j, i));

    return result;
}

template<typename T>
int matrix<T>::getRank() {
    lg << "--Calculating rank for:\n" << *this << '\n';
    if (rank != -1) return rank;
    rank = r;
    matrix result = rref_called ? *rref : getRREF();
    for (int i = result.r - 1; i >= 0; i--) {
        for (int j = 0; j < result.c; j++) {
            if (result.get(i, j)) return rank;
        }
        rank--;
    }
    return rank;
}

template<typename T>
T matrix<T>::getTrace() {
    lg << "--Calculating trace for:\n" << *this << '\n';
    if (r != c) {
        throw DimensionError("Matrix is not square\n");
    }
    trace = zero;
    for (int i = 0; i < r; i++) {
        trace = trace + get(i, i);
    }
    return trace;
}

template<typename T>
matrix<T> matrix<T>::operator+(matrix &t) {
    lg << "--Calculating A+B with A:\n" << *this << "B:\n" << t << '\n';
    matrix result(r, c);
    for (int i = 0; i < r; i++) {
        for (int j = 0; j < c; j++) {
            result.set(i, j, get(i, j) + t.get(i, j));
        }
    }
    return result;
}

template<typename T>
matrix<T> matrix<T>::operator-(matrix &t) {
    lg << "--Calculating A+B with A:\n" << *this << "B:\n" << t << '\n';
    matrix result(r, c);
    for (int i = 0; i < r; i++) {
        for (int j = 0; j < c; j++) {
            result.set(i, j, get(i, j) - t.get(i, j));
        }
    }
    return result;
}

template<typename T>
matrix<T> matrix<T>::operator*(matrix &t) {
    lg << "--Calculating A*B with A:\n" << *this << "B:\n" << t << '\n';
    if (t.r != c) {
        throw DimensionError("Invalid multiplication operation\n");
    }
    matrix result(r, t.c);
    for (int k = 0; k < r; k++) {
        for (int i = 0; i < t.c; i++) {
            T sum = get(k, 0) * t.get(0, i);
            for (int j = 1; j < c; j++) {
                sum = sum + get(k, j) * t.get(j, i);
            }
            result.set(k, i, sum);
        }
    }
    return result;
}

template<typename T>
matrix<T> matrix<T>::operator/(matrix &t) {
    lg << "--Calculating A*Inverse(B) with A:\n" << *this << "B:\n" << t << '\n';
    return *this * t.getInverse();
}


template<typename T>
matrix<T> matrix<T>::getRREF() {
    lg << "--Calculating RREF for:\n" << *this << '\n';
    if (rref_called) return *rref;
    rref_called = true;
    rref = new matrix<T>(*this);
    inverse = new matrix(getIdentity(r));

    for (int t = 0; t < r - 1; t++) {
        /// Pivoting
        T mx = rref->get(t, t);
        int mxi = t;
        for (int i = t + 1; i < r; i++) {
            if (std::abs(rref->get(i, t)) > mx) {
                mx = std::abs(rref->get(i, t));
                mxi = i;
            }
        }
        if (mxi != t) {
            determinant = determinant * -one;
            for (int j = 0; j < c; j++) {
                T tmp = rref->get(t, j);
                rref->set(t, j, rref->get(mxi, j));
                rref->set(mxi, j, tmp);

                tmp = inverse->get(t, j);
                inverse->set(t, j, inverse->get(mxi, j));
                inverse->set(mxi, j, tmp);
            }
        }

        lg << "Pivoting:\n" << *rref;

        /// Forward Elimination
        for (int i = t + 1; i < r; i++) {
            if (rref->get(t, t) == zero) continue;
            T e = -rref->get(i, t) / rref->get(t, t);
            lg << "R" << i + 1 << " += " << e << " * R" << t + 1 << '\n';
            for (int j = 0; j < c; j++) {
                rref->set(i, j, rref->get(i, j) + e * rref->get(t, j));
                inverse->set(i, j, inverse->get(i, j) + e * inverse->get(t, j));
                if (std::abs(rref->get(i, j)) < error) rref->set(i, j, zero);
                if (std::abs(inverse->get(i, j)) < error) inverse->set(i, j, zero);
            }
            lg << *rref;
        }

    }

    for (int i = 0; i < r; i++) {
        determinant = determinant * rref->get(i, i);
    }

    /// Backward Elimination
    for (int t = r - 1; t >= 0; t--) {
        for (int i = t - 1; i >= 0; i--) {
            if (rref->get(t, t) == zero) continue;
            if (rref->get(t, t)) {
                T e = -rref->get(i, t) / rref->get(t, t);
                lg << "R" << i + 1 << " += " << e << " * R" << t + 1 << '\n';
                for (int j = 0; j < c; j++) {
                    rref->set(i, j, rref->get(i, j) + e * rref->get(t, j));
                    inverse->set(i, j, inverse->get(i, j) + e * inverse->get(t, j));
                    if (std::abs(rref->get(i, j)) < error) rref->set(i, j, zero);
                    if (std::abs(inverse->get(i, j)) < error) inverse->set(i, j, zero);
                }
                lg << *rref;
            }
        }
    }

    /// Diagonal Normalization
    for (int i = 0; i < r; i++) {
        T diag = rref->get(i, i);
        if (diag != zero) lg << "Dividing R" << i + 1 << " by " << diag << '\n';
        else continue;
        for (int j = 0; j < c; j++) {
            rref->set(i, j, rref->get(i, j) / diag);
            inverse->set(i, j, inverse->get(i, j) / diag);
            if (std::abs(rref->get(i, j)) < error) rref->set(i, j, zero);
            if (std::abs(inverse->get(i, j)) < error) inverse->set(i, j, zero);

        }
        lg << *rref;
    }


    return *rref;
}
