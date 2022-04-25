#include<bits/stdc++.h>
using namespace std;

vector<int> factorize(int n)
{
    vector<int>v;
    for (int i=1; i<=sqrt(n); i++)
    {
        if (n%i == 0)
        {
            if (n/i == i)
                v.push_back(i);
            else
                {v.push_back(i);v.push_back(n/i);}
        }
    }
    return v;
}

int main(){
	for(auto u:factorize(50))cout<<u<<" ";
}