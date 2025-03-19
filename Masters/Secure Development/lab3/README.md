# Lab 3 - Fuzzing

> Ahmed Nouralla - a.shaaban@innopolis.universty

[TOC]

## 0. Prerequisites

1. Create `vulnerable.c` with the provided content

   ```bash
   mkdir src
   nano src/vulnerable.c
   ```

1. Run AFL++ in docker, mapping the `src` directory to the container

   ```bash
   docker run -ti -v .:/src aflplusplus/aflplusplus
   ```

4. Result

   ![image-20250221211304382](https://i.imgur.com/gKLFgGb.png)

5. Compile the code with `afl-gcc-fast`

   ![image-20250221224316291](https://i.imgur.com/8LIebLO.png)

## 1. Understand the Application

1. The application does the following actions first:
   - Takes a file as an command-line argument and opens it
   - Reserve an `input` buffer of fixed size (1024 character)
   - Use `fread` function to read the file, then `fclose` to close it afterwards.
   - Sets the last position in the buffer to the null `\0` character
   - Passes the `input` buffer to a `vulnerable_function`
1. `vulnerable_function` does the following:
   - Creates a `buffer` of fixed 100 characters
   - Uses `strcpy` to copy data from the larger buffer `input` to the smaller buffer
   - `strcpy` is known to be **unsafe** because it does not do bound checks when copying buffer.
   - This means that, for a large enough input, the program may write data that fills the buffer and overwrites adjacent memory locations as well (buffer overflow).
1. Why buffer overflow is dangerous?
   - Overwriting adjacent memory may lead to unpredictable behavior, crashes (DoS), or even allow arbitrary code execution if exploited carefully.
1. How can buffer overflow be exploited
   - One can overwrite the return address of the function, changing the flow of execution to run code located an attacker-controlled area of the memory (e.g., to run `/bin/sh`)
   - Upon getting a shell, one may escalate and perform more malicious actions (e.g., installing malware).

## 2. Run the Fuzzer

- Prepared test input (short, moderate, and long text, also some binary data)

  ![image-20250221212953747](https://i.imgur.com/ygeMqkn.png)

- If we want the fuzzer to have a stable (reproducible) random behavior, we can specify a seed with the `-s` flag.

- Running the fuzzer with `afl-fuzz`

  ![image-20250221212124178](https://i.imgur.com/sZ3zLav.png)

- Fuzzer started

  ![image-20250221210131519](https://i.imgur.com/wD28T2r.png)

## 3. Monitor the fuzzing process

- After 5 minutes, the state written below changed to "finished"

  ![image-20250221214734302](https://i.imgur.com/l2OPJoT.png)

## 4. Analyze the results

- Fuzzer generates statistics in `fuzzer_stats`. Showing file contents:

  ![image-20250221215156598](https://i.imgur.com/IKxspHn.png)

- It shows a saved crash, input that caused it is stored in `output/crashes`. It shows mutated data from the input file `3.txt` created above. The file contained a long string that caused a buffer overflow.

  ![image-20250221221420921](https://i.imgur.com/qhJ5aJx.png)

- We can reproduce the crash in `gdb` using the crash file as input to the program.

  ![image-20250221221530502](https://i.imgur.com/gthr5dW.png)

## 5. Fix the vulnerability

- Modify the code in the vulnerable_function to use a safer function `strncpy` that does bound checking.

  ```c++
  void updated_function() {
      char buffer[100];
  
      // Use strncpy instead of strcpy to prevent buffer overflow
      strncpy(buffer, input, sizeof(buffer) - 1); // Copy at most 99 characters
  
      buffer[sizeof(buffer) - 1] = '\0'; // Ensure null-termination
  }
  ```

- Recompiling and rerunning the fuzzer with the same input, the crash no longer occurs

  ![image-20250221224908608](https://i.imgur.com/FwXnJJE.png)

## 6. Theoretical Questions

1. What is the purpose of fuzzing in software security testing?
   - Fuzzing is used to discover software vulnerabilities by providing random, malformed, or unexpected inputs to a program, aiming to trigger crashes, hangs, or unintended behavior.
1. How does AFL generate test cases to find vulnerabilities?
   - AFL mutates initial seed inputs (e.g., flipping bits, adding/removing bytes) and uses coverage-guided feedback to prioritize inputs that explore new code paths, increasing the likelihood of finding vulnerabilities.
1. What other types of vulnerabilities can fuzzing detect besides buffer overflows?
   - Fuzzers may detect Memory leaks, use-after-free, integer overflows, format string vulnerabilities, race conditions, and logical errors.
1. How can you improve the efficiency of a fuzzing campaign?
   - Use parallel fuzzing, provide high-quality seed inputs, tune mutation strategies, limit memory usage, and focus on specific code paths or functions. Tools like AFL++ or libFuzzer can also enhance efficiency.

