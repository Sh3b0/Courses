/*
    Tester userspace application.
    Resizes mydev stack by writing the new size using ioctl()
*/

#include <sys/ioctl.h>
#include <fcntl.h>
#include <unistd.h>
#include <stdio.h>
#include <errno.h>
#include <inttypes.h>

#define MAJOR_NUM 100 // mydev major number

int main() {
    int fd = open("/dev/mydev", O_WRONLY);
    unsigned long new_size;
    if(fd < 0) {
        printf("Failed to open /dev/mydev\n");
        return -1;
    }
    printf("Enter the new stack size: ");
    if(scanf("%lu", &new_size) != 1) {
        printf("Invalid input\n");
        return -1;
    }
    printf("Requested size: %lu\n", new_size);
    if(ioctl(fd, _IOW(MAJOR_NUM, 0, unsigned long), new_size) < 0) {
        printf("ioctl failed with errno: %d\n", errno);
        return -1;
    }
    close(fd);
    printf("ioctl succeeded\n");
    return 0;
}
