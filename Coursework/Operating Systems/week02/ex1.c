#include <stdio.h>
#include <limits.h>
#include <float.h>

int main()
{
    int a = INT_MAX;
    float b = FLT_MAX;
    double c = DBL_MAX;
    printf("int max = %d\nfloat max = %f\ndouble max = %f\n", a, b, c);
    printf("size of int = %lu\nsize of float = %lu\nsize of double = %lu\n", sizeof(a), sizeof(b), sizeof(c));
}
