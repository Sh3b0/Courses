#include <bits/stdc++.h>
using namespace std;

/// Sieve of Eratosthenes, Pre-calculating Primes, O(n*log(log(n)))
map<int,bool>np; ///Not Prime

void sieve(int n)
{
    np[0] = np[1] = 1;
    for(int i=2;i<=n;i++){
        if(!np[i]){
            /// primes.push_back(i);
            for(int j=i*i;j<=n;j+=i){
                np[j]=1;
            }
        }
    }
}
int main()
{
    sieve(1000);
    cout<<!np[17];
}
