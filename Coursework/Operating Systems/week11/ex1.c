#include <stdio.h>
#include <string.h>
#include <sys/mman.h>
#include <fcntl.h>
#include <unistd.h>

int main()
{
    // Open file for R/W
    int fd = open("ex1.txt", O_RDWR);

    if(fd == -1){
        printf("Error opening file\n");
        return 1;
    }

    // Constant text
    const char* text = "This is a nice day";

    // Get text size
    const int ssz = strlen(text);

    // Resize the file
    if(ftruncate(fd, ssz) == -1){
        printf("Error truncating file\n");
        return 1;
    }

    // Memory map
    char *map = mmap(0, ssz-1, PROT_WRITE, MAP_SHARED, fd, 0);
    if(map == MAP_FAILED){
        printf("Error mapping file\n");
        return 1;
    }

    // Write text
    strncpy(map, text, ssz);

    // Memory unmap
    if(munmap(map, ssz) == -1){
        printf("Error unmapping file\n");
        return 1;
    }

    // Close file
    close(fd);

    printf("Success\n");
}
