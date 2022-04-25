#include <stdio.h>
#include <stdint.h>

#define MAX_PAGE_ID 1000

uint8_t R[MAX_PAGE_ID + 1];

struct page
{
    int id;
    uint8_t cnt;
};

int main(int argc, char** argv)
{
    int n = 100;
    if(argc > 2)
    {
        printf("Wrong number of arguments\n");
        return 1;
    }
    sscanf(argv[1], "%d", &n);
    //printf("n=%d\n", n);

    FILE* fp = fopen("ex1.txt", "r");
    if(fp == NULL)
    {
        printf("Error opening file\n");
        return 1;
    }

    struct page PT[n]; // Page Table
    for(int i=0;i<n;i++) PT[i].id=0;

    //printf("test: %d\n", PT[0].id);
    int h=0, m=0, id;

    while(fscanf(fp, "%d", &id) != EOF)
    {
        R[id] = 1; // mark this page as referenced.

        struct page* found = NULL;
        for(int i=0; i<n; i++)
        {
            if(PT[i].id == id)
            {
                found = &PT[i];
                break;
            }
        }

        if(found != NULL)  // if page is found in the table, it's a hit
        {
            //printf("Page %d FOUND\n", id);
            h++;
        }
        else   // otherwise, it's a miss (page fault), evict the page with the lowest count
        {
            m++;
            //printf("Page %d not found\n", id);
            // find the lowest count page
            uint8_t mn = 255;
            int I;
            for(int i=0; i<n; i++)
            {
                if(PT[i].id == 0)
                {
                    I = i;
                    break;
                }
                if(PT[i].cnt < mn)
                {
                    mn = PT[i].cnt;
                    I = i;
                }
            }

            // claim that page
            R[I] = 0;
            PT[I].id = id;
            PT[I].cnt = 0;
        }


        // shift counters one bit to the right.
        // Add R to the leftmost bit.
        for(int i=0; i<n; i++)
        {
            PT[i].cnt = PT[i].cnt >> 1;
            PT[i].cnt = PT[i].cnt | (R[i] << 8);
        }

    }

    printf("Hits: %d, Misses: %d\n", h, m);
    printf("Hits/Misses = %f\n", h/(double)m);
}
