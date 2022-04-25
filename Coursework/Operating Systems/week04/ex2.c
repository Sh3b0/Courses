#include <stdio.h>
#include <unistd.h>

int main()
{
    int n = 3;
    // Each fork() splits the process into two.
    // For n forks we get 2^n process

    for(int i=0; i<n; i++)
    {
        fork();
        sleep(5);
    }
}

