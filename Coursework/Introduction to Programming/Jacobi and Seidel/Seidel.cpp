#include<bits/stdc++.h>

using namespace std;

class Matrix {
public:
    int n, m;
    map< pair<int,int>, double > x;

    Matrix(int r, int c)
    {
        this -> n = r;
        this -> m = c;
    }

    Matrix operator + (Matrix t)
    {
        Matrix r(n,m);
        for(int i=0; i<n; i++)
        {
            for(int j=0; j<m; j++)
            {
                r.x[{i,j}]=x[{i,j}]+t.x[{i,j}];
            }
        }
        return r;
    }

    Matrix operator - (Matrix t)
    {
        Matrix r(n,m);
        for(int i=0; i<n; i++)
        {
            for(int j=0; j<m; j++)
            {
                r.x[{i,j}]=x[{i,j}]-t.x[{i,j}];
            }
        }
        return r;
    }

    Matrix operator * (Matrix t)
    {
        Matrix r(n, t.m);
        for(int k = 0; k < n; k++)
        {
            for(int i = 0; i < t.m; i++)
            {
                double sum = 0.0;
                for(int j = 0; j < m; j++)
                {
                    sum += x[{k,j}] * t.x[{j,i}];
                }
                r.x[{k,i}] = sum;
            }
        }
        return r;
    }

    double magnitude() { /// this.m should be 1
        double res = 0.0;
        for(int i=0;i<n;i++){
            res += x[{i,0}] * x[{i,0}];
        }
        return sqrt(res);
    }

    Matrix Inv()
    {
        Matrix id(n, n);
        for(int i = 0; i < n; i++){
            for(int j = 0; j < n; j++){
                if(i == j) id.x[{i, j}] = 1;
                else id.x[{i, j}] = 0;
            }
        }

        for(int t = 0; t < n - 1; t++)
        {
            /// --------------  Pivoting ---------------
            int I = t;
            double mx = x[{t, t}];
            for(int i = t + 1; i < n; i++)
            {
                if(abs(x[{i, t}]) > mx)
                {
                    mx = abs(x[{i, t}]);
                    I = i;
                }
            }
            if(I != t)
            {
                for(int j = 0; j < n; j++)
                {
                    swap(x[{t, j}], x[{I, j}]);
                    swap(id.x[{t, j}], id.x[{I, j}]);
                }
            }

            /// --------- Forward Elimination ----------
            for(int i = t + 1; i < n; i++)
            {
                double T = -x[{i, t}] / x[{t, t}];
                for(int j = 0; j < n; j++)
                {
                    x[{i ,j}] += T * x[{t, j}];
                    id.x[{i, j}] += T * id.x[{t, j}];
                }
            }
        }

        /// -------------- Way Back ---------------
        for(int t = n - 1; t >= 0; t--)
        {
            for(int i = t - 1; i >= 0; i--)
            {
                double T = -x[{i, t}] / x[{t, t}];
                for(int j = 0; j < n; j++)
                {
                    x[{i, j}] += T * x[{t, j}];
                    id.x[{i, j}] += T * id.x[{t, j}];
                }
            }
        }
        /// ------- Diagonal Normalization --------
        for(int i = 0; i < n; i++)
        {
            for(int j = 0; j < n; j++)
            {
                id.x[{i, j}] /= x[{i, i}];
            }
            x[{i, i}] = 1.0;
        }

        return id;
    }

    friend istream &operator>>( istream  &input, Matrix &t )
    {
        for(int i=0; i<t.n; i++)
        {
            for(int j=0; j<t.m; j++)
            {
                input>>t.x[{i,j}];
            }
        }
        return input;
    }

    friend ostream &operator << (ostream &output, Matrix& t)
    {
        for(int i = 0; i < t.n; i++)
        {
            for(int j = 0; j < t.m - 1; j++)
            {
                output << fixed << setprecision(4) << t.x[{i, j}] << ' ' ;
            }
            output << fixed << setprecision(4) << t.x[{i, t.m-1}] << '\n' ;
        }
        return output;
    }

};


class IdMatrix : public Matrix{
public:
    IdMatrix(int n):Matrix(n,n){
        for(int i=0;i<n;i++){
            for(int j=0;j<n;j++){
                if(i==j)x[{i,j}]=1;
                else x[{i,j}]=0;
            }
        }
    }
};


int main()
{
    // freopen("o.txt","w",stdout);
    int n;
    cin>>n;
    Matrix A(n, n), alpha(n, n), b(n, 1), beta(n, 1);
    double e;

    cin>>A>>n>>b>>e;

    for(int i=0;i<n;i++){
        for(int j=0;j<n;j++){
            if(i!=j) { alpha.x[{i,j}] = 0.0; A.x[{i,j}] *= -1.0; }
            else if(A.x[{i,i}]) { alpha.x[{i,i}] = 1.0 / A.x[{i,i}]; A.x[{i,i}] = 0.0;}
            else return cout<<"The method is not applicable!\n", 0;
        }
    }
    /// alpha is now = the inverse of the diagonal matrix of A
    /// A is now =  - (L + U)
    beta = alpha * b;
    alpha = alpha * A;

    ostringstream o;
    o<<"beta:\n"<<beta<<"alpha:\n"<<alpha;
    Matrix B(n,n), C(n,n);

    for(int i=0;i<n;i++){
        for(int j=0;j<n;j++){
            if(i<=j){
                C.x[{i,j}] = alpha.x[{i,j}];
                B.x[{i,j}] = 0.0;
            }
            else{
                C.x[{i,j}] = 0.0;
                B.x[{i,j}] = alpha.x[{i,j}];
            }
        }
    }

    IdMatrix I(n);
    o<<"B:\n"<<B<<"C:\n"<<C<<"I-B:\n";
    B = (I-B);
    o<<B;
    B = B.Inv();
    o<<"(I-B)^-1:\n"<<B;

    int i = 0;
    double error = DBL_MAX, last_error = DBL_MAX;
    Matrix x = beta;
    while(error >= e) {
        o << "x(" << i++ << "):\n" << x;
        Matrix last = x;
        x = B * (C * last + beta);
        last = last - x;
        error = last.magnitude();
        if(error > last_error) return cout<<"The method is not applicable!\n", 0;
        last_error = error;
        o << "e: " << error << '\n';
    }
    o << "x(" << i << "):\n" << x;
    cout<<o.str();
}