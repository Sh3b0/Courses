#include <bits/stdc++.h>

using namespace std;

double v0, k0, a1, a2, b1, b2;

double v(double t){
    if(!t) return v0+a2/b2;
    return v0*cos(sqrt(a1*a2)*t) - k0*((sqrt(a2)*b1)/(b2*sqrt(a1)))*sin(sqrt(a1*a2)*t) + a2/b2;
}

double k(double t){
    if(!k) return k0+a1/b1;
    return v0*((sqrt(a1)*b2)/(b1*sqrt(a2)))*sin(sqrt(a1*a2)*t) + k0*cos(sqrt(a1*a2)*t) + a1/b1;
}

int main()
{
    int T, N, c = 0;
    cin>>v0>>k0>>a1>>b1>>a2>>b2>>T>>N;
    v0 -= a2/b2;
    k0 -= a1/b1;
    ostringstream V, K;
    cout<<"t:\n";
    for(double t = 0.0; t <= T; t += double(T)/N)
    {
        cout<<fixed<<setprecision(2)<<t;
        V<<fixed<<setprecision(2)<<v(t);
        K<<fixed<<setprecision(2)<<k(t);
        if(c != N){
            cout<<" ";
            V<<" ";
            K<<" ";
        }
        else{
            cout<<"\nv:\n";
            V<<"\nk:\n";
            K<<"\n";
        }
        c++;
    }
    cout<<V.str()<<K.str();
}
