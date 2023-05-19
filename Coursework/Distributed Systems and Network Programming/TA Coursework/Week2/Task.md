# Week 2 - Concurrency and Parallelism

> Distributed Systems and Network Programming - Spring 2023

## Task

Your tasks for this lab:

- Write a multi-threaded TCP server that communicates with a given client
- Optimize the runtime of the client using [threading](https://docs.python.org/3/library/threading.html) and [multiprocessing](https://docs.python.org/3/library/multiprocessing.html)

## Server Implementation

- The server should do the following:
  1. Accept a client connection
  1. Spawn a new thread to handle the connection
  1. Generate a random 10x10 image (you can use [pillow](https://python-pillow.org/) module for that)
  1. Send the image to the connected client, then close that connection


- Additional requirements:


  - The server should stay listening all the time and should not terminate unless a `KeyboardInterrupt` is received.

  - The server should be able to handle multiple connections simultaneously.

  - The server socket is marked for address reuse so that the OS would immediately release the bound address after server termination. You can do so by calling the `setsockopt` on the server socket before binding the address as follows:

    ```python
    server_socket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
    server_socket.bind((SERVER_IP, SERVER_PORT))
    ```


## Client Implementation

The client does the following:

1. Connect to the TCP server multiple times to download 5000 images, one by one.
2. Download the images to a directory called `frames` (creating the directory if it does not exist).
3. Create a GIF by combining the downloaded frames.
4. Use [time](https://docs.python.org/3/library/time.html) module to calculate the total time taken for frame download and GIF generation.

## Your task

1. Once you understand how the client code works, start by writing the server.
2. Once the server works fine. It's time to optimize the runtime of the client.
3. Use `threading` to spawn multiple threads that download the required frames concurrently.
4. Use `multiprocessing` to spawn multiple processes (not more that your CPU core count) to process the frames in parallel to create the GIF faster. You may use `multiprocessing.Pool()` to achieve the task.
5. Check the time taken in each stage and verify that the client runtime was improved.

## Example run

```bash
$ python3 NameSurname_server.py
Listening on 0.0.0.0:1234
Sent an image to (127.0.0.1, 50125)
Sent an image to (127.0.0.1, 58754)
...

# Before optimizing client
$ python3 NameSurname_client.py
Frames download time: 25.516422748565674
GIF creation time: 30.278062343597412

# After optimizing client
$ python3 NameSurname_client.py
Frames download time: 18.751099348068237
GIF creation time: 9.695139408111572
```

## Checklist

Your submitted code should satisfy the following requirements. Failing to satisfy an item will result in partial grade deduction or an assignment failure (depending on the severity).

- [ ] Two submitted files named according to the format `NameSurname_client.py` and `NameSurname_server.py`
- [ ] The source code executes successfully under the [latest stable Python interpreter](https://www.python.org/downloads/).
- [ ] The code does not use any external dependencies (apart from [pillow](https://python-pillow.org/))
- [ ] The code is readable and nicely formatted (e.g., according to [PEP8](https://peps.python.org/pep-0008/))
- [ ] The client runtime is improved after using `threading` and `multiprocessing`
- [ ] The source code is the author's original work. Both parties will be penalized for detected plagiarism.

