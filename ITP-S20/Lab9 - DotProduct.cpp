#include <bits/stdc++.h>

using namespace std;

template <typename A, typename B>
int DotProduct(A a, B b)
{
    int r = 0, c1 = 0, c2;
    for(auto u : a){
        c1++;
        c2=0;
        for(auto v : b){
            c2++;
            if(c1==c2){
                r+=u*v;
                break;
            }
        }
    }
    return r;
}
int main()
{

    vector<int>v1, v2;
    v1.push_back(1);v1.push_back(2);v1.push_back(3);
    v2.push_back(1);v2.push_back(2);v2.push_back(3);

    cout<<DotProduct(v1,v2)<<endl;

    multiset<int>s1, s2;
    s1.insert(1);s1.insert(2);s1.insert(3);
    s2.insert(1);s2.insert(2);s2.insert(3);

    cout<<DotProduct(s1,s2)<<endl;
}
