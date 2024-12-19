#include <stdio.h>
#include <sys/ipc.h>
#include <sys/shm.h>
#include <sys/types.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <string.h>
#include <stdlib.h>

int main(void)
{
    int shmid;
    key_t key;
    pid_t pid;
    char *s_addr, *p_addr;
    key = ftok("./a.c", 'a');
    shmid = shmget(key, 1024, 0777 | IPC_CREAT);
    if (shmid < 0)
    {
        printf("shmget is error\n");
        return -1;
    }
    printf("shmget is ok and shmid is %d\n", shmid);
    pid = fork();
    if (pid > 0)
    {
        p_addr = shmat(shmid, NULL, 0);
        strncpy(p_addr, "hello", 5);
        wait(NULL);
        exit(0);
    }
    if (pid == 0)
    {
        sleep(2);
        s_addr = shmat(shmid, NULL, 0);
        printf("s_addr is %s\n", s_addr);
        exit(0);
    }
    return 0;
}

