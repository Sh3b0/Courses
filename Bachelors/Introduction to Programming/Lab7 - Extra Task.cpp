#include<bits/stdc++.h>

using namespace std;

class Fraction
{
public:
    int w, num, den; /// Whole number, Numerator, Denominator

    /// ------------------- CLASS CONSTRUCTORS ---------------------

    Fraction() /// Default constructor.
    {
        w=0;
        num=0;
        den=1;
    }
    Fraction(int a,int b)  /// Primary constructor.
    {
        w=0;
        num=a;
        den=b;
    }
    Fraction(int a)  /// Integer constructor.
    {
        w=0;
        num=a;
        den=1;
    }
    Fraction(double d)  /// Double constructor.
    {
        w = floor(d);
        d -= w;
        ostringstream o;
        o<<d;
        string s = o.str();
        s.erase(0,2);
        num = atoi(s.c_str());
        den =  Ten_Power(s.size());
    }

    /// ------------------- OVERLOADING ARITHMETIC OPERATORS --------------
    Fraction operator + (Fraction t)
    {
        t.num += t.w * t.den;
        num += w * den;

        Fraction r(num * t.den + t.num * den, den * t.den);
        return r;
    }

    Fraction operator * (Fraction t)
    {
        t.num += t.w * t.den;
        num += w * den;
        Fraction r(num*t.num, den*t.den);
        return r;
    }
    /// ------------------- OVERLOADING TYPECAST OPERATORS --------------
    operator int()
    {
        num += w * den;
        return num/den;
    }
    operator double()
    {
        num += w * den;
        return double(num)/den;
    }
    /// ------------------- OVERLOADING INPUT/OUTPUT OPERATORS --------------------
    friend ostream& operator << (ostream& out, Fraction& t)
    {
        bool neg=0;
        if(t.num<0) neg=1, t.num=abs(t.num);
        if(t.w<0) neg=1, t.w=abs(t.w);

        t.euclid();
        if(t.den==1)
        {
            out<<t.num;
        }
        else if(t.num>t.den || t.w)
        {
            while(t.num>t.den)
            {
                t.num-=t.den;
                t.w++;
            }
            if(t.num==t.den)
            {
                t.w++;
                if(neg)out<<"-";
                out<<t.w;
            }
            else
            {
                t.euclid();
                if(neg)out<<"-";
                out<<t.w<<"."<<t.num<<"/"<<t.den;
            }
        }
        else
        {
            t.euclid();
            if(neg)out<<"-";
            out<<t.num<<"/"<<t.den;
        }
        return out;
    }
    friend istream &operator>>( istream  &input, Fraction &t )
    {
        string s;
        input>>s;
        if(Fraction::isInt(s)){ /// Check if fraction is an integer
            Fraction f(atoi(s.c_str()));
            t.w = f.w;
            t.num = f.num;
            t.den = f.den;
        }
        else if(Fraction::isDouble(s)) /// Check if fraction is double
        {
            const char* c = s.c_str();
            char* e = 0;
            Fraction f(strtod(c, &e));
            t.w = f.w;
            t.num = f.num;
            t.den = f.den;
        }
        else
        {
            string tmp = "";
            stringstream i(s);
            if(s.find('.')!=string::npos)  /// w.num/den
            {
                getline(i,tmp,'.');
                t.w=atoi(tmp.c_str());
                getline(i,tmp,'/');
                t.num=atoi(tmp.c_str());
                getline(i,tmp,'/');
                t.den=atoi(tmp.c_str());
            }
            else if(s.find('/')!=string::npos) /// num/den
            {
                getline(i,tmp,'/');
                t.num=atoi(tmp.c_str());
                getline(i,tmp,'/');
                t.den=atoi(tmp.c_str());
            }
            else /// num
            {
                t.w=0;
                t.num=atoi(s.c_str());
                t.den=1;
            }
            // cout<<t.w<<" "<<t.num<<" "<<t.den<<'\n';
        }
        return input;
    }
    /// ------------------- HELPER FUNCTIONS --------------------
    int gcd(int a,int b)
    {
        return (!b)? a : gcd(b, a%b);
    }
    void euclid()
    {
        int r = gcd(num, den);
        num/=r;
        den/=r;
    }
    int Ten_Power(int x)
    {
        int r=1;
        while(x--) r*=10;
        return r;
    }
    static bool isDouble(string s)
    {
        long double ld;
        return((istringstream(s) >> ld >> ws).eof());
    }
    static bool isInt(string s)
    {
        for(int i=0;i<s.size();i++){
            if(!isdigit(s[i]))return 0;
        }
        return 1;
    }
};


template<typename T> class Matrix
{
public:
    T x[20][20];
    int n,m;

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

    Matrix operator * (Matrix t)
    {
        if(m!=t.n)
        {
            cout<<"Invalid operation\n";
            exit(0);
        }

        Matrix r(n,t.m);
        for(int k=0; k<n; k++)
        {
            for(int i=0; i<t.m; i++)
            {
                T sum;
                for(int j=0; j<m; j++)
                {
                    // cout<<"Now multiplying "<<x[k][j]<<" by "<<t.x[j][i]<<endl;
                    sum = sum + x[k][j] * t.x[j][i];
                }
                r.x[k][i]=sum;
            }
        }
        return r;
    }

    /// ------------------------ Overloading Typecast on Matrices ------------------------------
    operator Matrix<Fraction>() { /// Casting matrix of Fraction on matrix of double.
        Matrix<Fraction>r(n,m);
        for(int i=0;i<n;i++){
            for(int j=0;j<m;j++){
                Fraction f(x[i][j]);
                r.x[i][j] = f;
            }
        }
        return r;
    }
    operator Matrix<double>() { /// Casting matrix of double on matrix of Fraction.
        Matrix<double> r(n,m);
        for(int i=0;i<n;i++){
            for(int j=0;j<m;j++){
                r.x[i][j] = double(x[i][j]);
            }
        }
        return r;
    }
    /// ------------------------- Overloading I/O on Matrices -------------------------
    friend ostream &operator<<( ostream &output, const Matrix& t)
    {
        T r;
        for(int i=0; i<t.n; i++)
        {
            for(int j=0; j<t.m-1; j++)
            {
                r = t.x[i][j];
                output<<r<<" ";
            }
            r = t.x[i][t.m-1];
            output<<r<<'\n';
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
    int n,m;

    cin>>n>>m;
    Matrix<double>m1(n,m);
    cin>>m1;

    Matrix<Fraction>m2(n,m);
    cin>>m2;

    cout<<m1 + Matrix<double>(m2);
    cout<<m2 + Matrix<Fraction>(m1);
}
