#include <bits/stdc++.h>
#define PI 3.14159265358979323846

using namespace std;

class Polar {
public:
    double r, t;
    Polar(double r, double t)
    {
        this->r = r;
        this->t = t;
    }
    Polar add(Polar that)
    {
        pair<double,double>c1 = this->toCartesian(), c2 = that.toCartesian();
        return Polar::toPolar(c1.first+c2.first, c1.second+c2.second);
    }
    Polar mul(Polar that)
    {
        double r1 = this->r, r2 = that.r;
        double t1 = this->t, t2 = that.t;
        Polar res(r1*r2, t1+t2);
        return res;
    }
    static Polar toPolar(double x, double y)
    {
        Polar res( sqrt(x*x+y*y), atan((y/x)*(PI/180)) );
        return res;
    }
    pair<double, double> toCartesian()
    {
        pair<double, double> res;
        res.first = this->r * cos(this->t);
        res.second = this->r * sin(this->t);
        return res;
    }
};
int main()
{
    /// Angles should be in Radian.
    Polar p1(2, PI/3);
    Polar p2(3, PI/6);
    cout<<p1.mul(p2).r<<" "<<p1.mul(p2).t<<endl;
}
