#include<bits/stdc++.h>

using namespace std;

/// Linear Search O(n)

int main()
{
    int x[] = {156, 61, 1, 981, -5};
    int n = sizeof(x)/sizeof(x[0]);
    int t = 1;

    vector<int>v;

    for(int i=0; i<n; i++)
    {
        if(x[i] == t) v.push_back(i);
    }

    if(v.empty())cout<<"Target not found in the array";
    else
    {
        cout<<"Found "<<v.size()<<" occurrence(s) of target at: ";
        for(int u:v)cout<<u<<" ";
    }

}
