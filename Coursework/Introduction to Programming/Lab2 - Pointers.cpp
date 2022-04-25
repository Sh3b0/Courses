#include<iostream>

using namespace std;

/// Declaring global integers n, m
/// n: number of rows in the 2D array
/// m: Number of columns in the 2D array
int n,m;

void print(int* p)
{
    /// Print all elements of the row pointed to by p., then print a new line
    for(int j=0; j<m-1; j++)
    {
        cout<<*(p+j)<<" ";
    }
    cout<<*(p+j-1)<<'\n';
}

int main()
{
    /// Input the size of the array
    cin>>n>>m;

    /// Declare a 2D array of integers of the given size.
    /// Supposing all array elements will not exceed INT_MAX
    int a[n][m];

    /// Input array elements row by row.
    for(int i=0; i<n; i++)
    {
        for(int j=0; j<m; j++)
        {
            cin>>a[i][j];
        }
    }

    /// Output array elements
    for(int i=0; i<n; i++)
    {
        /// Sending a pointer to the first element in the i-th row to the function print.
        /// The function will print the whole row then a new line.
        print(*(a+i));
    }
}
