#include <bits/stdc++.h>

using namespace std;

/// Bucket Sort O(n^2)

int main()
{
    float x[] = {0.78, 0.17, 0.39, 0.26, 0.72, 0.94, 0.21, 0.12, 0.23, 0.68};
    int n = sizeof(x) / sizeof(x[0]);
    unordered_map<int,vector<float> >bucket;
    for(int i=0; i<n; i++)
    {
        bucket[floor(n*x[i])].push_back(x[i]);
    }
    vector<float> result;
    for(int i=0; i<n; i++)
    {
        if(!bucket[i].empty())
        {
            sort(bucket[i].begin(),bucket[i].end()); /// Supposed to use insertion sort here.
            for(auto u:bucket[i]) result.push_back(u);
        }
    }
    for(auto u:result)cout<<u<<" ";
}