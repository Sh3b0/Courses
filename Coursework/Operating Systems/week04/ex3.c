#include <stdio.h>
#include <stdlib.h>

int main()
{
    char command[1000];
    printf("Pythoff Shell 1.0.0, all rights reserved\nPress Ctrl+C to exit\n");
    while(1)
    {
        printf(">>> ");
        scanf("%s", command);
        system(command);
    }
}

