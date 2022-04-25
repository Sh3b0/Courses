#include <bits/stdc++.h>
using namespace std;

/// Count Sort O(n + mx), stable, out of place
/// Won't work for negative numbers or numbers with high max.

int main()
{
    int x[] = {5,4,5,2,1};
    int n = sizeof(x)/sizeof(x[0]);

    int mx = *max_element(x,x+n);           /// O(n)

    int m[mx+1], y[n];
    memset(m,0,sizeof(m));                  /// O(n)

    for(int i=0;i<n;i++) m[x[i]]++;         /// O(n)

    for(int i=1;i<=mx;i++) m[i]+=m[i-1];    /// O(mx)

    for(int i=0;i<n;i++){                   /// O(n)
        y[m[x[i]]-1]=x[i];
        m[x[i]]--;
    }

    for(int u:y)cout<<u<<" ";
}