#include <bits/stdc++.h>

using namespace std;

/// Insertion Sort O(N^2)

int main() {
    int x[] = {68, 894, 15, -20 , 8};
    int n = sizeof(x)/sizeof(x[0]);

    for(int i=1;i<n;i++){
        for(int j=i;j>0;j--){
            if(x[j]<x[j-1])
                swap(x[j],x[j-1]);
            else break;
        }
    }

    for(auto u:x)cout<<u<<" ";
}
