# SNA Lab 1: **Introduction to Linux** 

## Exercise 1 Questions

1. What is /etc/apt/sources.list.d directory stands for? How can you use it? Provide an example.

   > Its usually used by 3rd party scripts for applications to add their repositories there for APT (Ubuntu's package manager) to access, deleting files from there won't have side effects, unlike /etc/apt/sources.list
   >
   > Adding a file with `.list` extension and a content similar to `deb <repo-url> <app-name> main` for Debian packages will add the repository to APT's index.

2. How can you add/delete 3rd party repositories to install required software? Provide an example.

   > In Ubuntu, user can add 3rd party repositories by adding the repository to their sources list so APT can access it, a typical example:
   >
   > 1. Get the repository's public key available at `<pk-url>` and adds it to the list of sources (key-ring) trusted by APT (btw, this is now deprecated for a security [reason](https://askubuntu.com/questions/1286545/what-commands-exactly-should-replace-the-deprecated-apt-key/1307181#1307181)) 
   >
   >    ```bash
   >    wget -q -O - <pk-url> | sudo apt-key add -
   >    ```
   >
   > 2. Creates file `<app-name>.list` in `/etc/apt/sources.list.d` that contains instructions for apt to access the repository available at `<repo-url>`.
   >
   >    ```bash
   >    sudo sh -c 'echo deb <repo-url> <app-name> main > /etc/apt/sources.list.d/<app-name>.list'
   >    ```
   >
   > 3. Update sources and install the application
   >
   >    ```
   >    sudo apt-get update
   >    sudo apt-get install <app-name>
   >    ```

3. When do you need to get public key for the usage of the remote repo? Provide an example.

   > When adding custom (3rd party) repositories, apt requires GNU Privacy Guard (GPG) public key used to digitally sign packages to ensure authenticity of packages being fetched from repositories. An example was given in the first step of the previous question.



## Exercise 2 Questions

1. Describe how “top” works. Explain all important fields in its output based on your system resources.

   > top (table of processes) is a task manager program in UNIX-like OS's, it displays an ordered list of running processes (ordering can be based on CPU/Memory utilization or other criteria)
   >
   > List is updated by the system periodically, many implementations with different features exist.
   >
   > Fields shown in Ubuntu:
   >
   > - PID: identifier of the process.
   > - USER: who initiated the process.
   > - %CPU: percentage of CPU the process is using.
   > - %MEM: percentage of Memory the process is using.
   > - COMMAND: the command used to run the process.
   > - S: process state.
   > - NI: NICE value (user-adjustable number for priority).
   > - PR: actual process priority number chosen by the kernel.
   > - VIRT: virtual size of process (sum of memory it reserved)
   > - RES: how much memory the process is actually consuming.
   > - SHR: how much of VIRT is sharable (like a software library)

2. Explain briefly each process states from the output of the “top” command.

   > R = runnable, executing in CPU, or waiting to be executed in run queue. 
   >
   > S = interruptible sleep, waiting for some event (non-IO) to complete
   >
   > D = un-interruptible sleep: waiting for I/O to complete.
   >
   > T = stopped by job control signal (e.g., SIGSTOP)
   >
   > t = stopped by debugger during trace (e.g., gdb)
   >
   > Z = zombie, completed execution, but still has an entry in process table.

3. What happens to a child process that dies and has no parent process to wait for it and what’s bad about this?

   > In general, processes that stay waiting for a long time are erroneous and introduce resource leak (they reserve an entry with a PID in process table while not using them).
   >
   > When a parent process dies, the child process is said to be "orphan", orphan processes are adopted by `init` (PID=1), a process that is a zombie and an orphan is automatically reaped (wiped, process entry is removed completely) by the system. 

4. How to know which process ID used by application?

   > Check the output of `ps` command (first column), you can pipe `|` the output to `grep <app-name>` for easier search.

5. What is a zombie process and what could be the cause of it? How to find and kill zombie process?

   > A zombie process is a process that has completed execution (terminated), but still has an entry in process table, the entry is needed by the process parent to read the child's (now zombie's) exit state. All child process become zombie first before being reaped (wiped completely).
   >
   > From `top` command we can see in the state `S` column the zombie processes having the value of `Z`. Process that are zombies are not affected by `kill` command, they are automatically reaped by the system when appropriate as described above.

