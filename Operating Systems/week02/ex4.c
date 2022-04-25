#include <stdio.h>

void swap(int *a, int *b)
{
    int c = *a;
    *a = *b;
    *b = c;
}

int main()
{
    int a, b;
    printf("Enter 2 integers: ");
    if(scanf("%d %d", &a, &b) == 2)
    {
        swap(&a, &b);
        printf("After swapping: %d %d\n", a, b);
    }
    else
    {
        printf("An error occurred\n");
    }
}

