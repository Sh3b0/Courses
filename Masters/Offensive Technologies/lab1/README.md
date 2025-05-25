# Lab 1 - Understanding Assembly

> Ahmed Nouralla - a.shaaban@innopolis.university

[TOC]

## Task 1 - Preparation

1. Using `ghidra` for disassembling the binaries.

2. Checked the binaries on [hybrid-analysis](https://www.hybrid-analysis.com/sample/51d1c6635af02f0d202fa07a643200a84bbfb9576d8b8aa4a22b47f527a82895) and verified they're safe to run.

   ![image-20250322223335015](https://i.imgur.com/KIThi7O.png)

## Task 2 - Theory

- **What is ASLR, and why do we need it?** [[ref.](https://en.wikipedia.org/wiki/Address_space_layout_randomization)]

  - Address Space Layout Randomization is a technique used by operating systems to mitigate memory-related exploits like buffer-overflow.
  - It works by randomizing the memory layout of a program each time it runs (changing base addresses of the stack, heap, and libraries).
  - This makes it more difficult for an attacker to predict the location of specific areas and craft malicious payloads.

- **What kind of files did you receive (e.g., 32bit/64bit)?** [[ref.](https://en.wikipedia.org/wiki/File_(command))]

  - Output of `file` command against the binaries of task 3 shows information about them (architecture, whether debug info is included, whether the binary is stripped, etc).

    ```bash
    $ file sample32 
    sample32: ELF 32-bit LSB pie executable, Intel 80386, version 1 (SYSV), dynamically linked, interpreter /lib/ld-linux.so.2, for GNU/Linux 3.2.0, BuildID[sha1]=14e300cb9d36dfdb6b23833164ecb1c1339d814c, with debug_info, not stripped
    
    $ file sample64
    sample64: ELF 64-bit LSB pie executable, x86-64, version 1 (SYSV), dynamically linked, interpreter /lib64/ld-linux-x86-64.so.2, for GNU/Linux 3.2.0, BuildID[sha1]=047527792d38bff77ab0d642cd31921bfe9fe1d2, with debug_info, not stripped
    
    $ file sample64-2
    sample64-2: ELF 64-bit LSB pie executable, x86-64, version 1 (SYSV), dynamically linked, interpreter /lib64/ld-linux-x86-64.so.2, for GNU/Linux 3.2.0, BuildID[sha1]=cb3d8fd741fd67fbdcf029696261b95faa9fd513, not stripped
    ```

  - One can also use `readelf` to extract information about the binaries' contents. Showing the header information for the `sample32` binary.

    ```yaml
    $ readelf -h sample32
    ELF Header:
      Magic:   7f 45 4c 46 01 01 01 00 00 00 00 00 00 00 00 00 
      Class:                             ELF32
      Data:                              2's complement, little endian
      Version:                           1 (current)
      OS/ABI:                            UNIX - System V
      ABI Version:                       0
      Type:                              DYN (Position-Independent Executable file)
      Machine:                           Intel 80386
      Version:                           0x1
      Entry point address:               0x410
      Start of program headers:          52 (bytes into file)
      Start of section headers:          8416 (bytes into file)
      Flags:                             0x0
      Size of this header:               52 (bytes)
      Size of program headers:           32 (bytes)
      Number of program headers:         9
      Size of section headers:           40 (bytes)
      Number of section headers:         34
      Section header string table index: 33
    ```

- **What do stripped binaries mean?** [[ref.](https://en.wikipedia.org/wiki/Strip_(Unix))]

  - A stripped binary is an executable program with debugging information removed (which are not needed for normal execution of the binary).
  - It typically results from running `strip` command on a binary file.
  - It reduces the size of the executable, may result in better performance, and makes it more difficult to disassemble or reverse the binary file.

- **What are GOT and PLT (in the context of assembly)?** [[ref.](https://refspecs.linuxfoundation.org/ELF/zSeries/lzsabi0_zSeries/x2251.html)]

  - A desired property for a binary is to work correctly regardless of where in memory it's loaded at runtime (i.e., [Position Independent Code](https://en.wikipedia.org/wiki/Position-independent_code)).
  - Global Offsets Table (GOT)
    - Redirects position-independent address calculations to absolute locations in memory.
    - The section `.got` maps symbols in source code to their corresponding absolute memory address at runtime.
  - Procedure Linkage Table (PLT)
    - Redirects position-independent function calls to absolute locations.
    - The section `.plt` containing stub code that either jumps to the actual function being called (if it has already been resolved) or calls the dynamic linker `ld.so` to resolve the function's address and update the `.got.plt` section.

- **How can the debugger insert a breakpoint in the debugged binary/application?**

  - When compiling binaries, one may include debugging information (`.debug_info`). E.g., using `-g` flag with `gcc`.
  - A debugger (e.g., `gdb`) may then use such information to place breakpoints that pause code execution at certain points by replacing the instruction at the target address with a special breakpoint instruction.
  - When the CPU encounters this instruction, it triggers a trap, transferring control to the debugger
  - The debugger dumps memory information about the current program state to help with code analysis and fixing bugs.

## Task 3 - Disassembly

**a. Disabled ASLR using the command**

```bash
sudo sysctl -w kernel.randomize_va_space=0
```

**b. Loaded the binaries in Ghidra. Showing analysis interface for `sample64`.**

- Check the [appendix](#Appendix) for more details about the shown interface and its windows.

![image-20250322210500447](https://i.imgur.com/yQ0I5Bq.png)

**c. Do the function prologue and epilogue differ between 32bit and 64bit?**

- `sample_function` prologue in the given 32-bit binary

  ```assembly
  push    ebp       ; Save old frame (base) pointer to return to after
  mov     ebp, esp  ; Get new frame pointer from stack pointer
  push    ebx       ; Preserve the content of ebx (must be done)
  sub     esp, 14h  ; Allocate 0x14 = 20 bytes on the stack for locals
  ```

- `sample_function` prologue in the given 64-bit binary

  ```assembly
  push    rbp         ; rbp is the 64-bit equivalent to ebp
  mov     rbp, rsp    ; rsp is the 64-bit equivalent to esp
  sub     rsp, 20h    ; Allocate 0x20 = 32 bytes on the stack for locals
  ```

- Epilogues do not seem to differ, both binaries use `leave`, followed by `ret`

  ```assembly
  ; "leave" is used as a shorthand for the first two instructions
  mov esp, ebp        ; free space for locals (rsp, rbp for 64-bit)
  pop ebp             ; restore old frame pointer (rbp for 64-bit)
  ret                 ; free parameter space and return.
  ```

**d. Do function calls differ in 32bit and 64bit? What about argument passing?** [[ref.](https://en.wikipedia.org/wiki/Calling_convention)]

- In the given binaries, `sample_function` doesn't take parameters and returns `void`.

  - A simple `CALL sample_function` is enough in this case.
  - However, it gets more complicated when there are arguments and return values.

- x86 calling example with `cdecl` for the function `int square(int x) { return x * x; }`

  ```assembly
  section .text
      global _start
  
  square:
      push ebp          ; Save the old base pointer
      mov ebp, esp      ; Set new base pointer
  
      mov eax, [ebp+8]  ; Get the argument (x) from the stack
      imul eax, eax     ; Compute x * x (result stored in eax)
  
      pop ebp           ; Restore the old base pointer
      ret               ; Return
  
  _start:
      ; Call square(5)
      push 5            ; Push the argument (5) onto the stack
      call square       ; Call the square function (result in eax)
      add esp, 4        ; Clean up the stack (cdecl requires caller to clean up)
  ...
  ```

- x86-64 example with `System V AMD64 ABI` for `long square(long x) { return x * x; }`

  ```assembly
  section .text
      global _start
  
  square:
      push rbp          ; Save the old base pointer
      mov rbp, rsp      ; Set new base pointer
  
      mov rax, rdi      ; Get the argument (x) from RDI
      imul rax, rax     ; Compute x * x (result stored in RAX)
  
      pop rbp           ; Restore the old base pointer
      ret               ; Return (result is in RAX)
  
  _start:
      mov rdi, 5        ; Pass the argument (5) in EDI (1st argument register)
      call square       ; Call the square function
  ...
  ```

- Key differences:

  |                  | x86 (cdecl)                                                  | x86-64 (System V)                                            |
  | ---------------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
  | Argument passing | All arguments are passed on the stack                        | First 6 integer arguments passed in registers. Rest on stack. |
  | Stack cleanup    | Caller responsible for cleaning up the stack after function call | No stack cleanup is needed for register-passed arguments.    |
  | Register names   | 32-bit registers (`EAX`, `EBX`, `ECX`, etc.).                | 64-bit registers (`RAX`, `RBX`, `RCX`, etc.). Can also access the 32-bit ones |

**e. What does the command `ldd` do?**

- List Dynamic Dependencies (`ldd`) is a UNIX command to print the shared libraries required by a given binary file (passed as an argument).

- Examples

  ```bash
  $ ldd sample32
  	linux-gate.so.1 (0xf7fc9000)
      libc.so.6 => /lib32/libc.so.6 (0xf7db9000)
  	/lib/ld-linux.so.2 (0xf7fcb000)
  	
  $ ldd sample64
  	linux-vdso.so.1 (0x00007ffff7fca000)
      libc.so.6 => /lib/x86_64-linux-gnu/libc.so.6 (0x00007ffff7be6000)
      /lib64/ld-linux-x86-64.so.2 (0x00007ffff7fcc000)
  ```

**f. Why in the `sample64-2` binary, the value of `i` didn’t change even if input was very long?**

- Unlike `sample64`, `sample64-2` is not vulnerable to a buffer overflow attack

  ![image-20250322211253128](https://i.imgur.com/zbooBSB.png)

- This is because the first binary was compiled with the flag `-fno-stack-protector` which disables "stack canaries" used for protection against buffer overflow attacks
  - Canary is a random value placed on the stack between local variables and the saved return address/base pointer.
  - Before the function returns, OS checks if the canary is corrupted (overwritten), then the program terminates with the shown error, preventing exploitation.

## Task 4 - Reversing

 ### 1. Binary showing date and time

![image-20250323003747684](https://i.imgur.com/ah3T4oJ.png)

![image-20250323003801293](https://i.imgur.com/Ma0sDDl.png)

- Disassembly contains references to library functions `localtime`, `printf`, and the literal `%04d-%02d-%02d %02d:%02d:%02d\n`. When run, it shows the date and time in specific format. 

- Connecting the dots and reconstructing the program.

  ```c
  #include <stdio.h>
  #include <time.h>
  
  int main(void) {
      time_t current_time = time(NULL);
      struct tm *local_time = localtime(&current_time);
      printf("%04d-%02d-%02d %02d:%02d:%02d\n",
             local_time->tm_year + 1900, // Fix: +1900 missing from original code
             local_time->tm_mon + 1, // Fix: +1 missing from original code
             local_time->tm_mday
             local_time->tm_hour
             local_time->tm_min,
             local_time->tm_sec);
      return 0;
  }
  ```

### 2,3. Binaries printing an array

![image-20250323004035982](https://i.imgur.com/GcL0Atl.png)

- Disassembly contains recognizable instructions such as:
  - `CMP` and `JLE` for loop condition
  - `ADD` for loop increment.
  - `MOV` for initialization, assignment, and multiplication by 2 for loop variable.
  - `CALL` to printf for printing results.

- When run, the program prints an array `0,2,4,6,...,38` in the format `a[%d]=%d\n`.

  ```c
  #include <stdio.h>
  
  int main(void) {
      int arr[20];
      int i;
      for (i = 0; i < 20; i++) {
          arr[i] = i * 2;
      }
      for (i = 0; i < 20; i++) {
          printf("a[%d]=%d\n", i, arr[i]);
      }
      return 0;
  }
  ```

- A noteworthy detail is that the binary includes code for stack protection (canary).
- Binaries `bin2` and `bin3` seem identical. Command `cmp bin2 bin3` returns 0.

### 4. Binary doing even/odd checking

![image-20250323003606632](https://i.imgur.com/5pxBkKl.png)

- Disassembly contains recognizable instructions such as:

  - `CALL` to `scanf` to read a number and `printf` to print verdict (odd/even).
  - `AND`: a bitwise AND of `0x1` with the Least Significant Bit (LSB) of a number can be used to check for odd/even since odd numbers have LSB=1 and even numbers have LSB=0
  - `TEST` and `JNZ` check result and jump if the result is 0 to print an "odd" verdict, or proceed and print an "even" verdict otherwise.

- Reconstruction

  ```c
  #include <stdio.h>
  
  int main(void) {
      int number;
      printf("Enter an integer: ");
      scanf("%d", &number);
      if (number % 2 == 0) {
          printf("%d is even.\n", number);
      } else {
          printf("%d is odd.\n", number);
      }
      return 0;
  }
  ```

### 5. Stripped Binary doing factorial calculation

![image-20250323010759472](https://i.imgur.com/HEzgk84.png)

- Disassembly contains recognizable instructions such as:

  - `CALL` to `scanf` and `printf` to read and write results
  - `TEST` and `JNS` to exit when `scanf` returns a negative value indicating error.
  - `IMUL` for doing the multiplication
  - `MOV` for accumulating factorial result in `local_18`.
  - `ADD` for loop increment.

- Reconstructed code

  ```c
  #include <stdio.h>
  
  int main(void) {
      int number;
      unsigned long long factorial = 1;
  	printf("Enter an integer: ");
      scanf("%d", &number);
      if (number < 0) {
          printf("Error!");
      } else {
          for (int i = 1; i <= number; i++) {
              factorial = i * factorial;
          }
  		printf("Result is %d = %llu\n", number, factorial);
      }
      return 0;
  }
  ```

### 6. Stripped and statically-linked binary doing integer addition

![image-20250323012510786](https://i.imgur.com/v5uc807.png)

- Observations:
  - The binary seem to be statically linked: `ldd ./bin6` prints `not a dynamic executable`
  - `FUN_0040f6e0` and `FUN_0040f6e0` correspond to `printf` and `scanf`, respectively.
  - To locate this snippet in the disassembler, I used the search feature to look for the string `Enter two integers` known to be present in the binary.
  - Essentially, the logic seems to be a simple addition of two integers and printing the result.

- Reconstructed code

  ```c
  #include <stdio.h>
  
  int main(void) {
      int num1, num2, result;
      printf("Enter two integers: ");
      scanf("%d %d", &num1, &num2);
      result = num1 + num2;
      printf("%d + %d = %d\n", num1, num2, result);
      return 0;
  }
  ```

### Verification

To verify successful reversing:

1. Compile the C code with GCC
   - Supply `-fstack-protector-all` for `bin5` and `bin6`.
   - Also, supply `-s -static` for `bin6`
1. Disassemble the resulting binaries
1. Compare results with the disassembled versions of the original binaries.

## Appendix

Notes about the shown Ghidra's interface:

- **Listing:** the main window for viewing and analyzing disassembled code and comments.
- **Program Tree:** lists sections to navigate the binary, including
  - `.bss`: uninitialized globals and static variables
  - `.data`: initialized globals and static variables
  - `.text`: executable code (machine instruction) of the program
  - `.got`, `.plt`, and `.debug_info`: explained above.
- **Symbol tree:** lists named entities used by the program, including
  - Imports: external symbols used by the binary like `printf` from `libc`
  - Exports: symbols exposed by the program for potential use by other programs or libraries.
  - Functions (e.g., `sample_function` defined in the sample binaries).
  - Labels (e.g., jump targets)
- **Data Type Manager:** lists data types used by the program, including
  - Built-in data types (e.g., `int`, `char`, ...)
  - User-defined data types (e.g., `structs`, `unions`, `enums`).
  - Imported data types from header files or other sources.
