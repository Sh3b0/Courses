#include <stdio.h>
#include <string.h>
#include <sys/mman.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <unistd.h>

int main()
{
    // Open files for R/W
    int f1d = open("ex1.txt", O_RDONLY);
    int f2d = open("ex1.memcpy.txt", O_RDWR | O_CREAT);

    struct stat st;
    fstat(f1d, &st);
    int ssz = st.st_size;

    if(f1d == -1 || f2d == -1){
        printf("Error opening file(s)\n");
        return 1;
    }

    // Resize the file
    if(ftruncate(f2d, ssz) == -1){
        printf("Error truncating file\n");
        return 1;
    }

    // Memory map
    char *map1 = mmap(0, ssz, PROT_WRITE, MAP_PRIVATE, f1d, 0);
    char *map2 = mmap(0, ssz, PROT_READ | PROT_WRITE, MAP_SHARED, f2d, 0);

    if(map1 == MAP_FAILED || map2 == MAP_FAILED){
        printf("Error mapping file(s)\n");
        return 1;
    }

    // Write text
    memcpy(map2, map1, ssz);

    // Memory unmap
    if(munmap(map1, ssz) == -1 || munmap(map2, ssz) == -1){
        printf("Error unmapping file(s)\n");
        return 1;
    }

    // Close file
    close(f1d);
    close(f2d);

    printf("Success");
}
