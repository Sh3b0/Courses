#include <bits/stdc++.h>

using namespace std;

int main()
{
    int x[] = {-2, -5, 6, -2, -3, 1, 5, -6}, n, mx = INT_MIN;
    ///                |-------------|
    n = sizeof(x)/sizeof(x[0]);

    for(int i=0;i<n;i++){
        for(int j=i+1;j<n;j++){
            int sum = 0;
            for(int k=i;k<=j;k++){
                sum+=x[k];
            }
            mx = max(mx, sum);
        }
    }

    cout << mx;
}
