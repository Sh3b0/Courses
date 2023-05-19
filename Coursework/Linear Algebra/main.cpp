#include "matrix/matrix.h"
#include <iostream>

using namespace std;

int main() {
    open_log();
    cout << "Test client started...\n";
    cout << "Enter a 3x3 matrix:\n";
    matrix<double> a(3, 3);
    cin >> a;
    try {
        cout << "Matrix Rank = " << a.getRank() << '\n';
        cout << "Matrix Trace = " << a.getTrace() << '\n';
        cout << "Matrix Determinant = " << a.getDeterminant() << '\n';
        cout << "Matrix in RREF:\n" << *new matrix<double>(a.getRREF());
        cout << "Matrix Transpose:\n" << *new matrix<double>(a.getTranspose());
        cout << "Matrix Inverse:\n" << *new matrix<double>(a.getInverse());
    }
    catch (error &e) {
        cout << e.what();
    }
    close_log();
}
