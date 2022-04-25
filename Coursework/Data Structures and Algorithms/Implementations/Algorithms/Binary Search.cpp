#include<bits/stdc++.h>

using namespace std;

/// Binary Search O(log(n))
/// If the array is not sorted, add the complexity of sorting the array

int main()
{
    int x[] = {156, 61, 1, 981, -5};
    int n = sizeof(x)/sizeof(x[0]);
    int t = -5;

    sort(x,x+n); /// MergeSort the array in O(n * log(n) )

    int l=0, r=n-1;

    while(l<=r){
        int m = (l+r)/2;

        if(x[m]>t) r = m-1;
        else if(x[m]<t) l = m+1;
        else return cout<<"Target found\n",0;
    }

    cout<<"Target not found\n";
}
