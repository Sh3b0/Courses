#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>

void* print_message(void* id)
{
	printf("Message from thread %d\n", *(int*)id);
	pthread_exit(NULL);
}

int main(int argc, char *argv[])
{
	int n = 5;

	for(int i=1; i<=n; i++)
	{
		pthread_t thread;
		pthread_create(&thread, NULL, print_message, &i);
		printf("Thread %d created\n", i);
		pthread_join(thread, NULL);
	}
	return 0;
}
