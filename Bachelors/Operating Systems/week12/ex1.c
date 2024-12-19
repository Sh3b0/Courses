#include <stdio.h>

int main()
{
	FILE *fp = fopen("/dev/random", "r");
    for(int i=0;i<20;i++)
	{
		printf("%c", fgetc(fp));
	}
	printf("\n");
	fclose(fp);
}
