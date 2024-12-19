# Task 2 - Character device

## Task

- Create simple Kernel Module with character device (in /dev) and basic operations (open/read/write/release).
- Implement simple data structure inside kernel: stack.
  - Use only statically allocated memory.
  - Maximum number of elements: 256.
  - Elements type: Signed integers (4 bytes long).
- `write()` operation should be equal to push, `read()` operation should be equal to pop.
- Return error in case of any failure (according to MAN, usually EFAULT).
- You must not allow device to be opened twice.

## Makefile

- To compile and load module: `sudo make`
- To run a simple test: `make test`
- To unload module and clean working directory: `sudo make clean`

## Tests

- Simple test:

    ![image-20220510194148403](../images/image-20220510194148403.png)

- Kernel logs: `tail -f /var/log/kern.log` or `sudo dmesg -w`

    ![image-20220510194315079](../images/image-20220510194315079.png)

