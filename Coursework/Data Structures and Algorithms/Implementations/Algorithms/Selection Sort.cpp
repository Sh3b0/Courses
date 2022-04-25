#include <bits/stdc++.h>

using namespace std;

/// Selection Sort O(N^2)

int main() {
    int x[] = {68, 894, 15, -20 , 8};
    int n = sizeof(x)/sizeof(x[0]);

    for(int i=0;i<n;i++){
        int mn=INT_MAX, mni;

        for(int j=i;j<n;j++){
            if(x[j]<mn){
                mn=x[j];
                mni=j;
            }
        }

        swap(x[mni],x[i]);
    }

    for(auto u:x)cout<<u<<" ";
}
