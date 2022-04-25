#include<iostream>

using namespace std;

/// HeapSort O( n * log(n) ), In-Place, Not Stable.

int x[] = {5, 9, 1, 0, -561, 69, 98, -20, 6}, size;

void swim(int k)
{
    if (k == 0) return;
    if (x[k] > x[k / 2]) swap(x[k], x[k/2]);
    swim(k / 2);
}

void sink(int k)
{
    int l = 2 * k, r = 2 * k + 1;
    if (l == size)
    {
        if (x[k] < x[l]) swap(x[k], x[l]);
    }
    else if (l < size)
    {
        int g;
        if (x[l] > x[r]) g = 2 * k;
        else g = 2 * k + 1;
        if (x[k] < x[g]) swap(x[k], x[g]);
        else return;
        sink(g);
    }
}

int main()
{
    size = sizeof(x)/sizeof(x[0]) - 1;
    for (int i = size; i >= 0; i--) swim(i);
    while (size > 0)
    {
        swap(x[0], x[size--]);
        sink(0);
    }
    for (auto i : x) cout<<i<<" ";
}
