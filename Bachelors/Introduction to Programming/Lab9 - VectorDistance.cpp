#include <bits/stdc++.h>

using namespace std;

double dist(vector<double> a, vector<double> b){
    vector<double> c;
    for(auto i1 = a.begin(), i2 = b.begin(); i1!=a.end(),i2!=b.end(); i1++, i2++){
        c.push_back(*i2-*i1);
    }
    double magn = 0;
    for(auto u:c){
        magn+=u*u;
    }
    return magn;
}

int main()
{
    vector<double>a,b;
    a.push_back(1.0);a.push_back(1.0);
    b.push_back(1.0);b.push_back(2.0);
    cout<<dist(a,b);
}
