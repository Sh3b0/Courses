---
marp: true
title: Week 4 Theory
---

# Serialization and Deserialization

- Serialization is the process of converting a programming construct (e.g., a data structure or an object) into a format that can be stored, transmitted over the network, and reconstructed later.
  - Popular serialization formats include XML, JSON and Protocol Buffers.
- Deserialization is the inverse process (i.e., converting the serialized format back into the original form).

---

# Remote Procedure Call (RPC)

- Remote Procedure Call allows executing remote code as if it's being stored and executed locally.
- This is typically needed for communication between microservices or distributed systems nodes.
- The caller (client) calls a stub function and the executor (server) runs the logic.
  - The stub function essentially serializes the request (including function name and parameters) and sends it over the network.
  - The executor function receives the request, deserialize it, run the application logic, serialize the results and sends them back.
- Popular implementation of this concept include Sun RPC, XML-RPC, JSON-RPC, and gRPC.

---

# gRPC

- gRPC is an open-source high performance RPC framework developed by Google.
- gRPC uses Protocol Buffers as the default data serialization format.
  - A single `.proto` file can be used to generate implementation (bindings) for multiple different programming languages.
  - These bindings allow storing the data in a language-neutral format.
- gRPC provides utilities for authentication, bidirectional streaming with flow control, blocking/nonblocking bindings, and cancellation/timeouts.

---

# gRPC-related Terminology

- `Server`   : gRPC server listening on a certain host and port.
- `Servicer` : the class containing all functions exposed for RPC.
- `.proto`   : standard format used to generate the stub and service files.
- `Service`  : a ProtoBuf construct for generating service file.
- `Message`  : a ProtoBuf construct for declaring message types for serialization and deserialization.
