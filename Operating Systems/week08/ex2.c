#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>

int main()
{
    for(int i=0;i<10;i++){
        void* x = malloc(1e7);
        memset(x, 0, sizeof(x));
        sleep(1);
    }
    // running this code in bg, vmstat 1 si and so fields remain 0s (nothing special)
    // running this code in bg, top shows that it's using much memory (obviously)
}

