#include <stdio.h>
#include <string.h>
#include <stdlib.h>

void vulnerable_function(char *input)
{
    char buffer[100];
    strncpy(buffer, input, sizeof(buffer) - 1);
    buffer[sizeof(buffer) - 1] = '\0';
}

int main(int argc, char *argv[])
{
    if (argc > 1)
    {
        FILE *file = fopen(argv[1], "r");
        if (!file)
        {
            perror("Error opening file");
            return 1;
        }

        char input[1024];
        size_t bytesRead = fread(input, 1, sizeof(input) - 1, file);
        fclose(file);

        input[bytesRead] = '\0';

        vulnerable_function(input);
    }
    else
    {
        printf("Usage: %s <filename>\n", argv[0]);
    }
    return 0;
}
