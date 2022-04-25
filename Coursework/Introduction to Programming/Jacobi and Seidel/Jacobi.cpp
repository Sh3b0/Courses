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
    o<<"alpha:\n"<<alpha<<"beta:\n"<<beta;

    int i = 0;
    double error = DBL_MAX, last_error = DBL_MAX;
    Matrix x = beta;
    while(error >= e) {
        o << "x(" << i++ << "):\n" << x;
        Matrix last = x;
        x = alpha * last + beta;
        last = last - x;
        error = last.magnitude();
        if(error > last_error) return cout<<"The method is not applicable!\n", 0;
        last_error = error;
        o << "e: " << error << '\n';
    }
    o << "x(" << i << "):\n" << x;
    cout<<o.str();
}
