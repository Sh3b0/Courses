#include <stdio.h>
#include <unistd.h>

int main()
{
	int n = fork();

	// n for the parent process = PID of the child process
	// n for the child process = 0
        // PID increases by 1 for each fork()

	if (n == 0) printf("Hello from child process [%d - %d]\n", getpid(), n);
	else if(n > 0) printf("Hello from parent process [%d - %d]\n", getpid(), n);
        else printf("ERROR: Couldn't create a child process\n");

	return 0;
}
