#include <stdio.h>
#include <stdbool.h>
#include <stdlib.h>
#include <pthread.h>

#define BUF_MAX 100000

int buf = 0;
bool p_slp = 0, c_slp = 0;

void *producer(void *arg)
{
    printf("Hi from producer\n");

    while(1)
    {

        if(p_slp && c_slp)
        {
            printf("Fatal race condition(\n");
            exit(1);
        }

        if(buf == BUF_MAX)
        {
            p_slp = 1;
            c_slp = 0;
        }

        if(!p_slp)
        {
            buf++;
            if(buf == rand() % BUF_MAX) // Printing messages at random moments
                printf("Producing...\n");
        }
    }
}

void *consumer(void *arg)
{
    printf("Hi from consumer\n");

    while(1)
    {

        if(p_slp && c_slp)
        {
            printf("Fatal race condition(\n");
            exit(1);
        }

        if(!buf)
        {
            c_slp = 1;
            p_slp = 0;
        }

        if(!c_slp)
        {
            buf--;
            if(buf == rand() % BUF_MAX) // Printing messages at random moments
                printf("Consuming...\n");
        }
    }
}

int main()
{
    pthread_t p, c;

    pthread_create(&p, NULL, producer, &p);
    pthread_create(&c, NULL, consumer, &c);

    pthread_join(p, NULL);
    pthread_join(c, NULL);
}
