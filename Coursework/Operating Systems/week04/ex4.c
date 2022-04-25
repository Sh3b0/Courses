#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>

#define MAX_SIZE 1000

int main ()
{

    char command[MAX_SIZE];
    char* argv[MAX_SIZE];

    printf("PythOff Shell 1.0.0, all rights reserved.\nPress Ctrl+C to exit.\n");

    while(1)
    {
        for(int i=0; i<MAX_SIZE; i++)
        {
            command[i] = '\0';
            argv[i] = NULL;
        }

        fgets(command, sizeof(command), stdin);

        int wc = 0, cc = 0;

        argv[0] = (char*) malloc(MAX_SIZE);
        for(int i=0; i<strlen(command)-1; i++)
        {
            // printf("processing character %c\n", command[i]);
            if(command[i] == ' ')
            {
                argv[wc++][cc] = '\0';
                argv[wc] = (char*) malloc(MAX_SIZE);
                cc = 0;
            }
            else
            {
                argv[wc][cc++] = command[i];
            }
        }
        if(fork() == 0)
        {
            execvp(argv[0], argv);
        }
    }
}

