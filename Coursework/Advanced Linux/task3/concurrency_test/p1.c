#include <fcntl.h>
#include <stdio.h>
#include <unistd.h>

int main() {
    FILE *fp = fopen("/dev/mydev", "r+");
    if(fp == NULL) {
        printf("Failed to open /dev/mydev\n");
        return -1;
    }
    int t = 5;
    while(t--) {
        fprintf(fp, "%d\n", 1);
        fflush(fp);
        sleep(1);
    }
    fclose(fp);
    return 0;
}
