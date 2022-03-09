#include <bits/stdc++.h>

using namespace std;

/// Knuth Shuffle O(N)

int main() {
    int x[] = {68, 894, 15, -20 , 8};
    int n = sizeof(x)/sizeof(x[0]);

    for(int i=0;i<n;i++){
        int r = rand()%(i+1);
        swap(x[i],x[r]);
    }

    for(auto u:x)cout<<u<<" ";
}
