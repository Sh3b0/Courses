---
marp: true
title: Week 2 Theory
---

# **Concurrency vs. Parallelism**

- **Parallelism:** multiple instructions executing at the same physical time instant on different cores.
- **Concurrency:** process lifetimes overlapping, but execution need not happen at the same instant. Typically implemented by the OS scheduler through context switches.

---

# **CPU-bound vs. I/O bound tasks**

- **I/O-bound** **tasks** wait for I/O by an interactive user or another system across the network.
  - Examples include file upload/download, user interactive text input, reading/writing data from disk.
- **CPU-bound** **tasks** wait for the CPU to complete a computation.
  - Examples include arithmetic and logical operations, or a register access.

---

# **Thread vs. Process**

- **Thread:** an executing stream of instructions and its CPU register context.
  - Threads are more lightweight to spawn and destroy.
  - Threads within a process cooperate to avoid wasting time on I/O bound tasks.
  - Threads offer concurrency, but not true parallelism.

- **Process:** an execution of a program, consisting of a virtual address space, one or more threads, and some OS kernel state.
  - A single program may spawn more than one process to collaborate on a CPU-bound task.
  - If all such processes are CPU-bound, then the logical choice is to spawn no more than the number of available CPU cores.

---

# **Blocking vs. non-blocking method calls**

- A blocking method call pauses the flow of program execution until the method returns.

- A non-blocking call returns immediately, *promising* that results will arrive in the *future*. It may also *callback* some function after it's finished.
  - Multiple programming constructs exist to achieve that behavior

---

# **Synchronous vs asynchronous functions**

- Typically implemented in programming languages through the `async`, `await` syntactic sugar.

- An `async` method does not spawn any additional threads or processes. It uses clever compiler optimizations to reorganize the code so that the application stays responsive while doing a time-consuming task.

---

# **Race conditions and mutual exclusions**

- **Race condition** occurs when the behavior of the system depends on the order of execution of uncontrollable events.
  - **Example:** two or more threads try to access the same resource or change the shared data at the same time
- **Mutual exclusion:** one of the ways to avoid race conditions by allowing only one thread to access a critical region while others wait.
  - **Mutex (lock):** one implementation of the mutual exclusion idea. A binary shared variable (lock) is used to control access to the resource (locked or unlocked).

---

# **Thread models (patterns) for handling connections from multiple clients**

- **Thread per connection**: server spawns a new thread every time a new client connects.
- **Thread per request:** server creates a new thread every time a new request is made (one connection can include several requests)
- **Delegation (Manager-Workers, thread per socket)**
  - Server (manager) accepts all clients at the same welcoming socket, then delegates the communication with each request from a client to a worker function `handle_client(conn)` that runs in a separate thread and uses a different socket.

