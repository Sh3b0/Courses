#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <sys/resource.h>

int main()
{
    struct rusage usage;
    for(int i=0; i<10; i++)
    {
        void* x = malloc(1e7);
        memset(x, 0, sizeof(x));
        getrusage(RUSAGE_SELF, &usage);
        printf("Used memory: %ld\n", usage.ru_ixrss + usage.ru_idrss + usage.ru_isrss);
        sleep(1);
    }
}

