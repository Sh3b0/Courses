#include <stdio.h>

int main()
{
    int n; // number of processes
    scanf("%d", &n);

    int a[n], b[n], w[n], t[n];
    float aw = 0.0, at = 0.0;
    // arrival, burst, waiting, turn around, average waiting, average turn around times, respectively.

    for(int i=0;i<n;i++){
        scanf("%d %d", &a[i], &b[i]);
    }
    w[0] = 0;
    for(int i=0;i<n;i++){
        if(i){
            w[i] = 0;
            for(int j=0;j<i;j++)
                w[i] += b[j];
            w[i] -= a[i];
        }
        // printf("\ta, b, w = %d %d %d\n", a[i], b[i], w[i]);
        t[i] = b[i] + w[i];
        aw += w[i];
        at += t[i];
        printf("Process %d:\n", i+1);
        printf("\tCompletion(Exit) time: %d\n", a[i] + t[i]);
        printf("\tTurn around time: %d\n", t[i]);
        printf("\tWaiting time: %d\n", w[i]);
    }
    aw /= n;
    at /= n;
    printf("Average turn around time: %f\n", at);
    printf("Average waiting time: %f\n", aw);
}
