#include <bits/stdc++.h>

using namespace std;

/// Shell Sort, Average complexity O(N^1.5)

int main() {
    int x[] = {68, 894, 15, -20 , 8};
    int n = sizeof(x)/sizeof(x[0]);

    int h=1;
    while(h<n/3)h=3*h+1;

    while(h>=1){
        for(int i=h;i<n;i++){
            for(int j=i;j>=h;j--){
                if(x[j]<x[j-h])
                    swap(x[j],x[j-h]);
            }
        }
        h/=3;
    }

    for(auto u:x)cout<<u<<" ";
}
