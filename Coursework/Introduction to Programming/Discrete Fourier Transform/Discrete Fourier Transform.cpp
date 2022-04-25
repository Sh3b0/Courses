#include <bits/stdc++.h>

using namespace std;
const double PI = 3.1415926536;

class ImagMat { /// Imaginary Matrix
public:
    int n, m;
    map< pair<int, int>, complex<long double> > x;

    ImagMat(int n, int m){
        this -> n = n;
        this -> m = m;
    }

    ImagMat operator * (ImagMat t)
    {
        ImagMat r(n, t.m);
        for(int k = 0; k < n; k++)
        {
            for(int i = 0; i < t.m; i++)
            {
                complex<long double> sum(0.0, 0.0);
                for(int j = 0; j < m; j++)
                {
                    sum += x[{k,j}] * t.x[{j,i}];
                }
                r.x[{k,i}] = sum;
            }
        }
        return r;
    }

    friend ostream &operator << (ostream &output, ImagMat& t)
    {
        for(int i = 0; i < t.n; i++)
        {
            for(int j = 0; j < t.m; j++)
            {
                if(abs(t.x[{i, j}].real()) < 1e-6) t.x[{i, j}].real(0.0);
                if(abs(t.x[{i, j}].imag()) < 1e-6) t.x[{i, j}].imag(0.0);
                /*
                long double tmp = t.x[{i, j}].real();
                t.x[{i, j}].real(t.x[{i, j}].imag());
                t.x[{i, j}].imag(tmp);
                */
                if(j != t.m - 1)output << t.x[{i, j}] << ' ' ;
                else output << t.x[{i, j}] << '\n' ;
            }
        }
        return output;
    }

};

int main()
{
    // given c, find y = Fc
    int t1, t2, n = 8;
    ImagMat c(n, 1), F(n, n), y(n, 1);

    for(int i=1;i<=n;i++){

        if(i != n) scanf("(%d,%d),", &t1, &t2);
        else scanf("(%d,%d)", &t1, &t2);

        c.x[{i, 0}] = *new complex<long double>(t1, t2);
    }

    for(int i=0;i<n;i++){
        for(int j=0;j<n;j++){
            F.x[{i, j}] = pow(polar(1.0, PI/4), i*j);
        }
    }

    y = F * c;
    cout<<y;
}
