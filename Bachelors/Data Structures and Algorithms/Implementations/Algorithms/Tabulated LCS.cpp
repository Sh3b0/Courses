#include <bits/stdc++.h>
using namespace std;

// Longest Common Subsequence Problem
// Wrote for submission to https://www.hackerrank.com/challenges/common-child/

int dp[5005][5005];

int main()
{
    string a,b;
    cin>>a>>b;
    int n = a.size(), m = b.size();
    for(int i=0;i<=n;i++){
        for(int j=0;j<=m;j++){
            if(i==0||j==0) dp[i][j] = 0;
            else if(a[i-1] == b[j-1]) dp[i][j] = 1 + dp[i-1][j-1];
            else dp[i][j] = max(dp[i][j-1], dp[i-1][j]);
        }
    }
    cout<<dp[n][m]<<'\n';
}
