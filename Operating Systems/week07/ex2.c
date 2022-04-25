#include <stdio.h>
#include <stdlib.h>

/*
 Write a C program that dynamically allocates
memory for an array of N integers, fills the
array with incremental values starting from 0,
prints the array and deallocates the memory.
Program should prompt the user to enter N
before allocating the memory.
*/

int main()
{
    int n;
    scanf("%d", &n);
    int* arr = (int*) malloc(n * sizeof(int));
    for(int i=0; i<n; i++)
    {
        arr[i]=i;
    }
    for(int i=0; i<n; i++)
    {
        printf("arr[%d]=%d\n", i, arr[i]);
    }
    free(arr);
}
