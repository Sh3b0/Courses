# Week 4 - Remote Procedure Call

> Distributed Systems and Network Programming - Spring 2023

## Overview

Your task for this lab is to use [gRPC](https://grpc.io/docs/what-is-grpc/core-concepts/) to remotely call functions defined on a server.

- The client calls stub functions to do some CRUD operations against a SQLite database.
- The server executes the corresponding implementations to modify the database content.
- Communication happens over the network, with [Protocol Buffers](https://protobuf.dev/overview/) as the data serialization format.

## Remote Functions

The server should expose the following functions for RPC, the logic for each function is explained below:

- `PutUser(user_id: int, user_name: str)`

  - Update the entry for the user with `user_id` to have the specified `user_name`
  - If such user does not exist, create one and insert it into the database
  - Return `True` on success and `False` on failure
- `DeleteUser(user_id: int)`
  - Delete the entry for the user with the supplied `user_id`
  - Return `True` on success and `False` on failure
- `GetUsers()`
  - Return a list of user objects (all users in the database).
  - Each object should have two accessible properties named `user_id` and `user_name`

## Task

1. Create a Python virtual environment and install the required external dependencies

   ```bash
   python3 -m venv venv
   source venv/bin/activate
   pip3 install grpcio grpcio-tools
   ```

2. Create `schema.proto` which defines the following:
   - The database `service` with remote functions that can be called through `rpc`.
   - Request/response `message` format for the client/server communication.

3. Compile the schema file to generate the stub and service source files (`schema_pb2_grpc.py` and `schema_pb2.py`) using the following command:

   ```bash
   python3 -m grpc_tools.protoc --proto_path=. --python_out=. --grpc_python_out=. schema.proto
   ```

4. Write the gRPC server code to do the following:

   - Create or overwrite a local database file `db.sqlite` in the current directory.
   - Initialize the database with an empty table `Users(id INTEGER, name STRING)`
   - Create a `grpc.server` that listens forever for client RPC requests and executes them.
     - The server should terminate gracefully whenever a `KeyboardInterrupt` is received.
     - Upon receiving a request from the client, the server should print the name of the function to be executed along with the supplied arguments.
   - Implement the functions for `PutUser`, `DeleteUser`, and `GetUsers` as explained above.

5. Run your gRPC server, then run the given client (you are not supposed to modify the client).

6. Verify that the database was populated by inspecting the file `db.sqlite` using your favorite SQLite viewer tool/extension.

## Example Run

```bash
$ python3 server.py
gRPC server is listening on 0.0.0.0:1234
PutUser(1, 'User1')
PutUser(2, 'User2')
PutUser(3, 'User3')
PutUser(4, 'User4')
PutUser(2, 'User2_updated')
DeleteUser(3)
GetUsers()
```

```bash
$ python3 client.py
PutUser(1, 'User1') = True
PutUser(2, 'User2') = True
PutUser(3, 'User3') = True
PutUser(4, 'User4') = True
PutUser(2, 'User2_updated') = True
DeleteUser(3) = True
GetUsers() = {1: 'User1', 2: 'User2_updated', 4: 'User4'}
```

## Checklist

- [ ] A single submitted file (`NameSurname.zip`) containing only `server.py` and `schema.proto`
- [ ] The code is formatted and does not use any external dependencies (apart from  `grpc`)
- [ ] The `schema.proto` file correctly compiles without errors or warnings.
- [ ] The server code executes successfully and prints the expected output.
- [ ] The created database file `db.sqlite` contains the expected data after applying all queries.
- [ ] The source code is the author's original work. Both parties will be penalized for detected plagiarism
