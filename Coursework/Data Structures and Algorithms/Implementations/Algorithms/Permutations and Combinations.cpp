#include <bits/stdc++.h>
typedef long long ll;

using namespace std;

ll P(int n, int k)
{
    if(k>n) return -1;
    long long p = 1;
    for(int i=0;i<k;i++){
        p *= (n-i);
    }
    return p;
}

ll mem[1001][1001];

ll C(ll n, ll k){
    if(k>n) return 0;
    if(k==0||k==n) return 1;
    if(mem[n][k]) return mem[n][k];
    return mem[n][k] = ( C(n-1, k-1) /* %MOD */ + C(n-1, k)/* %MOD */ ) /* %MOD */;
}

int main()
{
    cout<<P(10, 2)<<'\n';
    cout<<C(10, 2)<<'\n';
}
