#include<iostream>

using namespace std;

class Matrix{
public:
    int x[20][20],n,m;

    Matrix(int r,int c)
    {
        this-> n = r;
        this-> m = c;
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

int main()
{
    int r,c;

    cin>>r>>c;
    Matrix A(r,c);
    cin>>A;

    cin>>r>>c;
    Matrix B(r,c);
    cin>>B;

    cin>>r>>c;
    Matrix C(r,c);
    cin>>C;

    Matrix D = A+B;
    Matrix E = B-A;
    Matrix F = C*A;
    Matrix G = A.Trn();

    cout<<D<<E<<F<<G;
}
