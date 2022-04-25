#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <stdbool.h>

int main()
{
    char* colors[] = {"\x1B[31m", "\x1B[32m", "\x1B[33m", "\x1B[34m", "\x1B[35m", "\x1B[36m", "\x1B[37m"};
    int stars = 1;
    bool q = true;
    while(1)
    {
        printf("%s", colors[rand()%7]);
        for(int i=0; i<stars; i++) printf("*");
        printf("\n");
        usleep(50000);
        q ? stars++ : stars--;
        if(stars == 20 || stars == 0) q = !q;
    }
}

