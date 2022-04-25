#include <stdio.h>
#include <string.h>

#define MAX 256

int main()
{
    char s[MAX];
    printf("Enter a string: ");
    if(fgets(s, sizeof(s), stdin) != NULL)
    {
    	printf("Reversed string: ");
        for(int i=strlen(s)-2; i>=0; i--)
        {
            printf("%c", s[i]);
        }
    	printf("\n");
    }
    else
    {
    	printf("An error occurred\n");
    	return 1;
    }
}

