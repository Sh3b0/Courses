#include <bits/stdc++.h>
using namespace std;

// Longest Common Subsequence Problem
// Wrote for submission to https://www.hackerrank.com/challenges/common-child/

int mem[5001][5001];
string a, b;

int lcs(int i, int j){
    if(i==-1||j==-1)return 0;
    // cout<<i<<" "<<j<<'\n';
    if(mem[i][j]) return mem[i][j];
    if(a[i] == b[j]) return mem[i][j] = 1 + lcs(i-1, j-1);
    else return mem[i][j] = max(lcs(i, j-1), lcs(i-1, j));
}

int main()
{
    ios_base::sync_with_stdio(0);cin.tie(0);cout.tie(0);
    cin>>a>>b;
    memset(mem,0,sizeof(mem));
    cout<<lcs(a.size()-1, b.size()-1)<<'\n';
    
}
