#include <bits/stdc++.h>

using namespace std;

/// Quick Sort O( n * log(n) )

int x[] = {51, 6851, -561, 841, 58, 64, 6}, n;

int partition(int s, int e)
{
    int co = s; /// Holds correct index of pivot

    for(int i = s; i < e; i++)
    {
        if(x[i] < x[e]) /// Last element was chosen as pivot.
        {
            swap(x[co++], x[i]);
        }
    }

    swap(x[e], x[co]);      /// Putting the pivot in the correct index
    return co;              /// Returns the correct index for the pivot
}

void qsort(int i,int j)
{
    if(j<=i)return;

    int p = partition(i,j);

    qsort(i, p-1);
    qsort(p+1, j);
}

int main()
{
    n = sizeof(x)/sizeof(x[0]);

    random_shuffle(x,x+n); /// To guarantee performance

    qsort(0,n-1);

    for(int u:x)cout<<u<<" ";
}
