#include <stdio.h>

int main()
{
    int mn, cnt = 0, time, n;
    double wt = 0, tat = 0, end, last = 0;
    scanf("%d", &n);

    int a[n], b[n], tmp[n];
    for(int i = 0; i < n; i++)
    {
        scanf("%d %d", &a[i], &b[i]);
        tmp[i] = b[i];
    }
    b[n] = 9999;
    for(time = 0; cnt != n; time++)
    {
        mn = n;
        for(int i = 0; i < n; i++)
        {
            if(a[i] <= time && b[i] < b[mn] && b[i] > 0)
                mn = i;
        }
        b[mn]--;
        if(b[mn] == 0)
        {
            cnt++;
            end = time + 1;
            wt += end - a[mn] - tmp[mn];
            tat += end - a[mn];
            printf("Process %d:\n", cnt);
            printf("\tWaiting time: %f\n", wt);
            printf("\tTurn around time: %f\n", tat - last);
            last = tat;
        }
    }
    printf("Completion Time: %f\n", end);
    printf("Average Waiting Time: %f\n", wt / n);
    printf("Average Turnaround Time: %f\n", tat / n);
}

