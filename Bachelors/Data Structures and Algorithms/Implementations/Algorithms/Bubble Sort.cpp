#include<bits/stdc++.h>
using namespace std;

/// Bubble Sort, O(n^2)

int main(){
    int x[]={156,61,1,981,-5};
	int n = sizeof(x)/sizeof(x[0]);
    bool s;

    while(1){
        s=0;
        for(int i=0;i<n-1;i++){
            if(x[i]>x[i+1])swap(x[i],x[i+1]),s=1;
        }
        if(!s)break;
    }

    for(auto u:x)cout<<u<<" ";
}
