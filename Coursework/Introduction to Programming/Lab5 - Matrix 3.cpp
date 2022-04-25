#include<iostream>

using namespace std;
int N;

class Matrix
{
public:
    int x[20][20],n,m;

    Matrix(int r,int c)
    {
        this->n = r;
        this->m = c;
    }

    Matrix operator + (Matrix t)
    {
        Matrix r(n,m);
        for(int i=0; i<n; i++)
        {
            for(int j=0; j<m; j++)
            {
                r.x[i][j]=x[i][j]+t.x[i][j];
            }
        }
        return r;
    }

    Matrix operator - (Matrix t) /// Our matrix - Some other matrix t
    {
        Matrix r(n,m);
        for(int i=0; i<n; i++)
        {
            for(int j=0; j<m; j++)
            {
                r.x[i][j]=x[i][j]-t.x[i][j];
            }
        }
        return r;
    }

    Matrix operator * (Matrix t) /// Our matrix x(n,m) * Some other matrix t(m,n)
    {
        if(m!=t.n)cout<<"Invalid operation\n";
        /// OUR M = THEIR N

        Matrix r(n,t.m);
        /// RESULT WILL BE r(OUR N, THEIR M)

        for(int k=0; k<n; k++) /// Passing on the same row n times
        {
            for(int i=0; i<t.m; i++)
            {
                int sum=0;
                for(int j=0; j<m; j++)
                {
                    sum += x[k][j] * t.x[j][i];
                }
                r.x[k][i]=sum;
            }
        }
        return r;
    }

    Matrix operator = (Matrix t)
    {
        Matrix r(n,m);
        for(int i=0; i<t.n; i++)
        {
            for(int j=0; j<t.m; j++)
            {
                r.x[i][j] = t.x[i][j];
            }
        }
        return r;
    }

    Matrix Trn()
    {
        Matrix r(m,n);
        for(int i=0; i< (this->m); i++)
        {
            for(int j=0; j< (this->n); j++)
            {
                r.x[i][j] = this->x[j][i];
            }
        }
        return r;
    }


    friend ostream &operator<<( ostream &output, const Matrix& t)
    {
        for(int i=0; i<t.n; i++)
        {
            for(int j=0; j<t.m-1; j++)
            {
                output<<t.x[i][j]<<" ";
            }
            output<<t.x[i][t.m-1]<<'\n';
        }
        return output;
    }

    friend istream &operator>>( istream  &input, Matrix &t )
    {
        for(int i=0; i<t.n; i++)
        {
            for(int j=0; j<t.m; j++)
            {
                input>>t.x[i][j];
            }
        }
        return input;
    }

};

/// Now Square matrix is supposed to inherit all public data members and member functions
/// Except constructors, destructors, friend member functions and overloaded operators,

class SqMatrix : public Matrix{
public:
    SqMatrix(int n):Matrix(n,n){}
};

class IdMatrix : public Matrix{
public:
    IdMatrix(int n):Matrix(n,n){
        for(int i=0;i<n;i++){
            for(int j=0;j<n;j++){
                if(i==j)x[i][j]=1;
                else x[i][j]=0;
            }
        }
    }
};

class EMatrix : public IdMatrix{
public:
    EMatrix(int I,int J,Matrix& t):IdMatrix(N){
        I--;J--;
        x[I][J]=-(t.x[I][J]/t.x[I-1][J]);
    }
};

class PMatrix : public IdMatrix{
public:
    PMatrix(int I,int J):IdMatrix(N){
        I--;J--;
        for(int i=0;i<N;i++){
            swap(x[I][i],x[J][i]);
        }
    }
};

int main()
{
    cin>>N;
    SqMatrix A(N);
    cin>>A;

    IdMatrix I(3);
    cout<<I;

    EMatrix E(2,1,A);
    cout<<E;

    Matrix C = E*A;
    cout<<C;

    PMatrix P(2,1);
    cout<<P;

    cout<<P*C;
}
