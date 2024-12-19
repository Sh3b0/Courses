#include <stdio.h>
#include <unistd.h>

#define MAX_SIZE 256

int main()
{
    char buf[MAX_SIZE];
    setvbuf(stdout, buf, _IOLBF, MAX_SIZE);

    printf("H");
    sleep(1);
    printf("e");
    sleep(1);
    printf("l");
    sleep(1);
    printf("l");
    sleep(1);
    printf("o");
    sleep(1);
    printf("\n");
}
