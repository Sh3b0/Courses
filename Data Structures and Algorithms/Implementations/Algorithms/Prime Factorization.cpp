#include<bits/stdc++.h>
using namespace std;

vector<int> factorize(int n)
{
    vector<int>v;
    while (n%2==0){
        v.push_back(2);
        n/=2;
    }

    for (int i=3;i<=sqrt(n);i+=2){
        while (n%i==0){
            v.push_back(i);
            n/=i;
        }
    }

    if(n>2) v.push_back(n);

    return v;
}

int main()
{
    for(auto u:factorize(120))cout<<u<<" ";
}
