#include<bits/stdc++.h>
using namespace std;

int main()
{
    int x[] = {-2, -5, 6, -2, -3, 1, 5, -6};
    ///                |-------------|
    
    int mx = a[0], cmx = a[0];
    for (int i = 1; i < size; i++)
    {
        cmx = max(a[i], cmx + a[i]);
        mx = max(mx, cmx);
    }
    cout << mx;
}