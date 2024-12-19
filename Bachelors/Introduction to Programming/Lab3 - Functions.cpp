#include<iostream>

using namespace std;

void f(int* a, int* b, int n)
{
    for(int i=0; i<n; i++)
    {
        int pro = 1;          /// To hold the product

        for(int j=0; j<n; j++)
        {
            if(i==j)continue; /// To skip multiplication when i=j
            pro *= *(a+j);    /// Multiply otherwise
        }

        *(b+i) = pro;         /// Assign result to current index in b
    }
}

/// Supposing all elements in both arrays will not exceed INT_MAX

int main()
{
    /// Declaring the array a
    int a[]= {2,1,5,9}; /// Change these values for testing

    /// To calculate how many elements in the array
    int n = sizeof(a)/sizeof(a[0]);

    /// Declaring b to have the same size as a
    int b[n];

    /// Applying the function
    f(a,b,n);

    /// Output the result
    for(int i=0; i<n; i++)
    {
        cout<<b[i]<<" ";
    }
}
