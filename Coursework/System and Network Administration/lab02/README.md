# SNA Lab 2: **Introduction to Linux**

## Exercise 3 Questions

1. Identify how many CPU are in your environment? What is a Linux kernel module?

   > 1. From the output of `htop` command, I can see that my system has 8 cores.
   >
   > 2. Kernel modules are pieces of code that can be loaded and unloaded into the kernel upon demand. They extend the functionality of the kernel without the need to reboot the system. Command `lsmod` shows currently loaded modules.

   

2. Which command will show statistics about your free/used memory? Describe all fields from the output of the command (for example point the difference between free and available)?

   > `free -h` shows a human-readable table of statistics about Memory and Swap space, the following fields are shown:
   >
   > - `total`: total size of memory.
   > - `used`: currently utilized (occupied) space.
   > - `free`: currently un-utilized space (wasted = not available for allocation by processes)
   > - `available`: is available for allocation by processes or usage by buffers/caches.
   > - `shared`: space common between processes (e.g., shared libraries)
   > - `buff/cache`: space used as a data buffer or cache.

   

3. If you list the content of directory using for example `ls -al` what do numbers in the column following after permissions information tell you?

   > They specify the number of hard links (aliases) to the corresponding file or folder.

   

4. What is the sticky bit? Show the file or directory with your configured sticky bit.

   > Sticky bit is one of the access right flags (like `r`, `w`) that has a special functionality
   >
   > - When assigned to files (i.e., `chmod +t <file>`, it makes the file sticks in memory even when it's no longer needed, to minimize swapping on this (this is now obsolete due to swapping optimizations)
   > - When assigned to a directory it makes all files/directories in it only renamable/removable by owner or root. Typically used with `/tmp` to prevent users from deleting/moving other users' files.

   

5. Which command will show the available disk space on the Unix/Linux system?

   > The output of `df` (disk-free) shows the available disk space for all mounted file systems. 

   

6. How to add a new system user without home directory and login?

   > `adduser --system --no-create-home USERNAME` or `useradd -r -s /sbin/nologin USERNAME`
   >
   > - The `-r` flag for `useradd` creates a system user without a `/home` directory.
   > - The `-s` allows specifying the login shell to use, which we set to `/sbin/nologin` 

   

7. Explain the differences among the following umask values: 000, 002, 022, 027, 077, and 277.

   > UNIX permission string, file permissions and `chmod` explained quickly:
   >
   > > 1st bit: `d` for directory, `-` for file, `s` for socket `p` for pipe, `D` for Door, `l` for symlinks, `c` for character special files.
   > >
   > > 1st triple: owner permissions, 2nd triple: group permissions, 3rd triple: everyone permissions.
   > >
   > > Numerical representation: `1=x, 2=w, 4=r`, thus: `3=-wx, 5=r-x, 6=rw-, 7=rwx`
   >
   > The value of `umask` is subtracted from the default file creation permission template.
   >
   >  - For directories, the base permissions are (777 = `rwxrwxrwx`)
   >  - For files they are (666 = `rw-rw-rw-`).
   >
   > Now, we can do the math and interpret it to human language.
   >
   > - 000 leaves everything unchanged
   > - 002 makes directories `rwxrwxr-x` and files `rw-rw-r--`
   > - 022 makes directories `rwxr-xr-x` and files `rw-r--r--`
   > - 027 `rwxr=x---` and files `rw-r-----`
   > - 077 makes directories `rwx------` and files `rw-------`
   > - 277 makes directories `r-x------` and files `r--------`

   

8. You have already configured swap in the exercise and the next step to increase or resize you swap space x2. Provide steps to do so. 

   > We previously created a swapfile of 1GB, let's make it 2GB.
   >
   > ```bash
   > # Turn swap off
   > # This moves stuff in swap to the main memory and might take several minutes
   > sudo swapoff -a
   > 
   > # Create an empty swapfile
   > # Note that "1G" is basically just the unit and count is an integer.
   > # Together, they define the size. In this case 2GB.
   > sudo dd if=/dev/zero of=/swapfile bs=1G count=2
   > 
   > # Set the correct permissions, read/write for creator.
   > sudo chmod 0600 /swapfile
   > 
   > sudo mkswap /swapfile  # Set up a Linux swap area
   > sudo swapon /swapfile  # Turn the swap on
   > ```

