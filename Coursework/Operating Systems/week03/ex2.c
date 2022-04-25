#include <stdio.h>
#include <stdbool.h>

void swap(int *a, int *b)
{
    int c = *a;
    *a = *b;
    *b = c;
}

void bubble_sort(int n, int x[])
{
    bool s = false;
    while(1)
    {
        s = 0;
        for(int i=0; i<n-1; i++)
        {
            if(x[i] > x[i+1])
            {
                swap(x+i, x+i+1);
                s = 1;
            }
        }
        if(!s) break;
    }
}

int main()
{
    int x[] = {4, 824, 0, -3, 513};
    int n = sizeof(x)/sizeof(x[0]);

    bubble_sort(n, x);

    for(int i=0; i<n; i++)
    {
        printf("%d ", x[i]);
    }
    printf("\n");
}
