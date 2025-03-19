# Lab 3 - Technological Communications

> Ahmed Nouralla - a.shaaban@innopolis.university

[TOC]

## Task 1

a. Define the following briefly

> Reference: https://books.google.ru/books/about/Modern_Operating_Systems.html

- **Process:** an abstraction/instance of a running program, including the current values of the program
  counter, registers, and variables.
- **Daemon: ** a software program or a process always running in the background waiting for
  client(s) requests, it forwards synchronous client requests to workers asynchronously.
- **System call: ** low-level functions that provide access to the underlying UNIX kernel [[ref.](https://www.cs.miami.edu/home/geoff/Courses/CSC521-04F/Content/UNIXProgramming/UNIXSystemCalls.shtml)].
- **Client & Server: **the most popular system architecture in which a server (a machine always running and listening) accepts, processes, and serves requests from different client machines.  
- **Peer to Peer: ** a different architecture that overcomes some of the problems of client/server by having each peer (node) acting as a server and client simultaneously (e.g., sharing a file in a decentralized way).

b. List and briefly explain the different types of Unix system calls for IPC.

> Reference: https://www.geeksforgeeks.org/inter-process-communication-ipc/

- **Pipes / Named pipes:** for communications between running processes on the machine.
- **Sockets**: for process communications over a network (can be the localhost).
- **Message queues:** provide a more organized way for IPC using queues.
- **Signals: **to send commands to a process to interrupt or terminate (i.e., `kill`) it.
- **Semaphores:** used to control process access to a shared resource like a file to prevent race condition.
- **Shared memory: **allowing processes to share a portion of system memory for data exchange.

c. Explain for at least 2 kernel architectures, how IPC is handled.

> Reference: https://www.geeksforgeeks.org/difference-between-microkernel-and-monolithic-kernel/

- In a monolithic kernel like Linux, IPC mechanisms mentioned above are tightly integrated into the kernel, making it fast but complex and harder to extend.
- In a microkernel like Mach, IPC is handled through message-passing, with the kernel only handling essential basic features and leaving high-level services to the userspace, making it more modular but with higher communication overhead. 

## Task 2

a. Define the various methods for Inter Process communication and provide advantages and disadvantages respectively.

> Reference: https://books.google.ru/books/about/Modern_Operating_Systems.html

- **Pipes** are simple and fast, but they are unidirectional, with buffer limitations and can only be opened between related (e.g., parent and child) processes. Named pipe overcome the last point, but still share the same other limitations.
- **Sockets** are powerful supporting remote and bidirectional communication over the network using a wide-range of protocols, yet they introduce complexity and overhead with potential increased latency.
- **Message queues** allow asynchronous and parallel communication, with additional features for prioritization, but are more complex to managed and introduce communication overhead.
- **Signals** are lightweight and asynchronous, but not much can be transferred and are generally harder to manage with no delivery guarantees.
- **Semaphores** can be complex to implement with potential deadlock risk. But if used correctly, they provide a powerful system for process synchronization.
- **Shared memory** mechanism efficiently allow shared access to data, but with potential of data corruption and complexity to manage synchronized access.

b. What IPC facilities are currently on your system? Show the current activity in them.

> Reference: https://man7.org/linux/man-pages/index.html

- `lsof` lists open files (pipes)

  ![image-20241110174448520](https://i.imgur.com/iubYXj4.png)

- `netstat -tuln` is used to see currently active TCP/UDP listening sockets

  ![image-20241110174141807](https://i.imgur.com/AkfJnIC.png)

- `ss -x` shows established UNIX sockets

  ![image-20241110174225247](https://i.imgur.com/QO2IlZx.png)

- `ipcs` command is used to show currently utilized message queues, shared memory segments and semaphore arrays

  ![image-20241110174129951](https://i.imgur.com/3xzs9Yt.png)

- `ps -eo pid,stat,comm` shows the signal **stat**us of a process (identified by its PID and command)

  ![image-20241110174824762](https://i.imgur.com/VSCVMT3.png)

c. Create two separate programs which implements inter process communication (between parent process and child process) using shared memory and pipes, using any programming language of your choice

> Reference: https://wiki.luckfox.com/Core3566/Linux-Systems-Programming/4.Inter-process-communication/

- C program illustrating shared memory.

  ```python
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
  ```

- C program illustrating pipes

  ```python
  #include <unistd.h>  
  #include <string.h>  
  #include <stdlib.h>  
  #include <stdio.h>  
  #include <sys/wait.h>  
        
  void sys_err(const char *str)  
  {  
      perror(str);  
      exit(1);  
  }  
        
  int main(void)  
  {  
      pid_t pid;  
      char buf[1024];  
      int fd[2];  
      char p[] = "test for pipe\n";  
            
      if (pipe(fd) == -1)   
          sys_err("pipe");  
        
      pid = fork();  
      if (pid < 0) {  
          sys_err("fork err");  
      }
      else if (pid == 0) {  
          close(fd[1]);  
          printf("child process wait to read:\n");
          int len = read(fd[0], buf, sizeof(buf));  
          write(STDOUT_FILENO, buf, len);  
          close(fd[0]);  
      }
      else {  
          close(fd[0]);  
          write(fd[1], p, strlen(p));  
          wait(NULL);  
          close(fd[1]);  
      }  
            
      return 0;  
  }
  ```

- Program execution. A video is also attached showing the code compilation and execution.

  ![image-20241110181326993](https://i.imgur.com/SdppUkm.png)

## Task 3

> Teammate: Mohamad Bahja - m.bahja@innopolis.university

### References

- Article explaining the difference between bind and reverse shell: https://www.geeksforgeeks.org/difference-between-bind-shell-and-reverse-shell/
- The website https://www.revshells.com/ provides payloads using different tools for opening a bind or a reverse shell. All the payloads used below were obtained from the website.

### Bind shells

Use `nc` and `powershell` to show a practical example with your teammate.

- A bind shell occurs when a victim's machine listens on a port and the attacker connects to it.

- Using `nc`

  - The example with `nc` shows the victim machine listening on port 1234 and executing `bash` whenever a client connects to it
  - The attacker machine connected to the listening victim and was able to execute commends remotely without having to enter a password.
  - Options `-e` and `-c` commonly used for bind and reverse shells were not available in the victim's version of `nc`.
  - To overcome that, a named pipe at `/tmp/f` is created using `mkfifo`. The attacker writes data to it, the victim reads commands from it, then sends results back over the netcat connection to the attacker.

  ![image-20241110225113201](https://i.postimg.cc/jd3hV2Rd/image.png)

- Using `powershell`, the same scenario could be created. The script is minified for efficiency, it essentially creates a TCP socket listener that reads commands from the remote host, executes them using `Invoke-Expression`, and send results back over the socket. 

  ![image-20241110225506231](https://i.postimg.cc/7ZCnq4L1/image.png)

### Reverse Shells

Use `nc`, `ncat`, `socat`, `powershell`, and `powercat` to show a practical example with your teammate

- A reverse shell occurs when the attacker machine is listening for a connection and the victim connects back to it. 

- **Using `nc`**

  - Options `-e` and `-c` commonly used for revshells were not available in victim's version of `nc`.
  - Here a trick with `mkfifo` is used

  ![image-20241110213635335](https://i.postimg.cc/DwtdZNW9/image.png)

- **Using `ncat`** (installed with `apt install ncat`)

  ![image-20241110214508586](https://i.postimg.cc/sXZ7QJgY/image.png)

- **Using `socat`** (installed with `apt install socat`)

  ![image-20241110214653355](https://i.postimg.cc/CL4Dq5SQ/image.png)

- **Using `powershell`** (installed using `sudo snap install powershell`)

  ![image-20241110221712450](https://i.postimg.cc/N0m8rxm5/image.png)

- **Using `powercat `**(installed from GitHub as shown)

  ![image-20241110222201109](https://i.postimg.cc/j2dym097/image.png)

b. List and give short explanations on the shell types in Linux.

- A shell is the command-line interface that is used to interact with the operating system by executing build-in or external programs. Popular shell programs include:
  - **Bourne Shell (SH)**: early implementation with limited features.
  - **Bourne Again Shell (BASH):** the most popular and default for many distributions, supports command history, tab-completion, support for conditional and functions.
  - **Z Shell (ZSH)**: extends bash with more features for customization, auto-completing, scripting, etc.
  - **Friendly Interactive Shell (FISH)** focus on being interactive with powerful auto suggestions and syntax highlighting. Used above in my screenshots.

c. What is netcat’s gaping security hole? Recreate and explain it.

- Gaping security hole is the security issue that allows netcat to be used as a back door, by having
  - The victim machine listening on some port and executing some shell whenever a client connects (e.g., `nc -l -p 1234 -e bash` )
  - Attacker connects to the machine (e.g., `nc <VICTIM_IP> 1234`) and gains passwordless Remote Code Execution (RCE).
- This is implemented in the bind shell example explained above.
