#include <stdio.h>

int main(int argc, char **argv)
{
    int n, stars = 1;
    if(argc == 2 && sscanf(argv[1], "%i", &n) == 1)
    {
        for(int i=n-1; i>=0; i--)
        {
            for(int j=0; j<i; j++) printf(" ");
            for(int j=0; j<stars; j++) printf("*");
            printf("\n");
            stars += 2;
        }
    	printf("\n");
    }
    else
    {
        printf("Usage: %s [N]\n", argv[0]);
        return 1;
    }
}

