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
        num=1;
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
    Fraction operator - (Fraction t)
    {
        t.num += t.w * t.den;
        num += w * den;
        Fraction r(num * t.den - t.num * den, den * t.den);
        return r;
    }
    Fraction operator -()
    {
        num += w * den;
        Fraction r(-num,den);
        return r;
    }
    Fraction operator * (Fraction t)
    {
        t.num += t.w * t.den;
        num += w * den;
        Fraction r(num*t.num, den*t.den);
        return r;
    }
    Fraction operator / (Fraction t)
    {
        t.num += t.w * t.den;
        num += w * den;
        Fraction r(num*t.den, den*t.num);
        return r;
    }
    /// ------------------- OVERLOADING COMPARISON OPERATORS --------------
    bool operator >(Fraction t)
    {
        Fraction r = *this - t;
        return r.num>0;
    }
    bool operator <(Fraction t)
    {
        Fraction r = *this - t;
        return r.num<0;
    }
    bool operator >=(Fraction t)
    {
        Fraction r = *this - t;
        return r.num>=0;
    }
    bool operator <=(Fraction t)
    {
        Fraction r = *this - t;
        return r.num<=0;
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
            if(neg)out<<"-";
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
                out<<t.w<<" "<<t.num<<"/"<<t.den;
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
        string s,tmp="";
        getline(input,s);
        stringstream i(s);

        //cout<<s<<endl;

        if(s.find(' ')!=-1)  /// w num/den
        {
            getline(i,tmp,' ');
            t.w=atoi(tmp.c_str());
            getline(i,tmp,'/');
            t.num=atoi(tmp.c_str());
            getline(i,tmp,'/');
            t.den=atoi(tmp.c_str());
        }
        else if(s.find('/')!=-1) /// num/den
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

};

int main()
{
    //freopen("i.txt","r",stdin);
    int t;
    double d;
    string s;
    while(1)
    {
        cin>>t;
        getline(cin,s);
        if(!t) return 0;
        else if(t==1||t==2||t==4||t==9)
        {
            Fraction f;
            cin >> f;
            if(t==1) cout<<f<<'\n';
            else if(t==2)cout<<int(f)<<'\n';
            else if(t==4)cout<<fixed<<setprecision(2)<<double(f)<<'\n';
            else if(t==9){
                Fraction r = -f;
                cout<<r<<'\n';
            }
        }
        else if(t==3)
        {
            cin>>d;
            Fraction f(d);
            cout<<f<<'\n';
        }
        else if(t==5||t==6||t==7||t==8||t==10)
        {
            Fraction a,b;
            cin>>a>>b;
            Fraction c = a + b, d = a - b, e = a * b, f = a / b;

            if(t==5) cout<<c<<'\n';
            else if(t==6) cout<<d<<'\n';
            else if(t==7) cout<<e<<'\n';
            else if(t==8) cout<<f<<'\n';
            else if(t==10)
            {
                if(a<b)cout<<a<<" is less than "<<b<<'\n';
                else if(a>b)cout<<a<<" is greater than "<<b<<'\n';
                else cout<<a<<" is equal to "<<b<<'\n';
            }
        }
    }
}
