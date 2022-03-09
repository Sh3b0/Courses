#include<iostream>

using namespace std;

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

int main()
{
    int n;
    cin>>n;
    Matrix* A = new SqMatrix(n);
    cin>>*A;

    cin>>n;
    Matrix* B = new SqMatrix(n);
    cin>>*B;

    cin>>n;
    Matrix* C = new SqMatrix(n);
    cin>>*C;

    Matrix d = *A+*B;
    Matrix* D = &d;
    SqMatrix* R1 = static_cast<SqMatrix*>(D);

    Matrix e = *B-*A;
    Matrix* E = &e;
    SqMatrix* R2 = static_cast<SqMatrix*>(E);

    Matrix f = *C**A;
    Matrix* F = &f;
    SqMatrix* R3 = static_cast<SqMatrix*>(F);

    Matrix g = A->Trn();
    Matrix* G = &g;
    SqMatrix* R4 = static_cast<SqMatrix*>(G);

    cout<<*R1<<*R2<<*R3<<*R4;
}
