#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <stdbool.h>

int main(int argc, char *argv[])
{
	bool append = false;
	if(argc > 1 && strcmp(argv[1], "-a") == 0)
		append = 1;

	int n = argc - append - 1;

    FILE **files = malloc(n * sizeof(FILE*));

	for(int i=0; i<n; i++)
	{
        if (append)
		    files[i] = fopen(argv[i+2], "a");
        else
            files[i] = fopen(argv[i+1], "w");
	}

	char buffer[128];
	while(fgets(buffer, 127, stdin) != NULL)
	{
		printf("%s", buffer);
		for(int i=0; i<n; i++)
		{
			fprintf(files[i], "%s", buffer);
		}
	}

	for(int i=0; i<n; i++)
	{
		fclose(files[i]);
	}

	free(files);
}
