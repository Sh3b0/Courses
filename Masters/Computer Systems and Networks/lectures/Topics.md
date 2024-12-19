# Computer Systems and Networks

> Hints on topics discussed during the lectures

[TOC]

## Lecture 1 - What Makes a System

- Early centralized systems
- The von Neumann architecture: CPU (CU, ALU), Memory, I/O
- System Architecture
  - Centralized: Single point of control (Failure).
  - Decentralized: No central location, or central decision maker.
  - Distributed: No central location, yet decision may still be centralized.
- Definitions
  - A system is a collection of components organized to accomplish a specific function or set of functions.
  - A system exists to fulfill one or more missions in its environment
    - No global clock. Need synchronization mechanisms
    - Each node can fail independently. Need fault tolerance mechanisms.
  - A distributed system is a collection of independent computers including software that appears to its users as a single coherent system;
    - Distribution transparency: hiding internal workings of distribution to end-user
      - Types: access, concurrency, replication, fault, location, migration, performance, scaling, performance.
    - Main issues: Communication, Coordination, Scalability, Resiliency

## Lecture 2 - Documenting System Architecture

- **System architecture:** a model that describes system structure and behavior, and possibly
  more (e.g. users, context, and other concepts).
  - It includes multiple structures. Each one represents a different view with a main focus.
  - Static structures describe the arrangement of design-time elements, while dynamic ones describe the runtime behavior and interaction between elements.
- **Architecture description:** a formal document which depicts a system with several diagrams, organized in a way that supports reasoning about the structure and behavior of the system
  - Why document? To design, reason about, guide and enforce implementation.
  - Non-functional requirements: performance, scalability, portability, reliability, maintainability, availability, security, ...
  - It may include (module/runtime, physica/logical) views, goals, (non-)functional requirements, decisions and justifications, and abstractions diagrams (e.g., class, component-connector, layers, packages, deployment).
  - C4 model: contexts, containers, components, classes
- **Responsibility-driven design:**
  - Characterized by contracts between system objects
    - DOING: what actions is the object responsible for.
    - KNOWING: what information does this object share.
  - Class-responsibility-collaboration (CRC) cards: brainstorming model when doing OOP.

## Lecture 3 - System Architecture Patterns

- Architecture diagrams
  - Free graphic notation
  - Context diagram
  - UML: use case, class, behavior, deployment, component/connector, ...
- Architecture Patterns
  - P2P
  - API Gateway
  - Pub/Sub
  - Request/Response
  - Event Sourcing
  - Extract/Transform/Load (ETL)
  - Batching vs. Streaming
  - Orchestration
- Layered Architecture
  - Access control to lower layers: Closed vs. Open alternatives
  - Examples: ISO/OSI (closed), OSF/Motif (Open)
- Partitioned (Tiered) Architecture
  - 1-tier (monolithic). 2-tiered: client/server. N-tier: client/server with separation of components for presentation/processing, and data management.

## Lecture 4 - Pipe and Filter

- Pipe and Filter: pattern for processing a stream of data by passing it through a combination of pipes (e.g.,  `|`) and filters (e.g., `grep`)
  - Example: processing combined data from multiple sensors.
- Shared data (repository): another pattern for processing data by having an independent shared data store that processes read/write to.

## Lecture 5 - Inter-Process Communication

- Network fallacies and their effect

  ![image-20241213165349682](/home/ahmed/.config/Typora/typora-user-images/image-20241213165349682.png)

- Main parameters of network performance: bandwidth, latency, loss, jitter

- Modes of IPC in UNIX:

  - Pipes / Named pipes: for communications between running processes on the machine.
  - Sockets: for process communications over a network (can be the localhost).
  - Message queues: provide a more organized way for IPC using queues.
  - Signals: to send commands to a process to interrupt or terminate (i.e., `kill`) it.
  - Semaphores: used to control process access to a shared resource like a file to prevent race condition.
  - Shared memory: allowing processes to share a portion of system memory for data exchange.

- Marshaling/Demarshaling and Serialization/Deserialization

  - XML, JSON, and YAML

## Lecture 6 - Inter-System Communication

- Distributed vs. Parallel Computing
  - Concurrency vs. Parallelism
- Remote invocation: command vs. event vs. query.
- Request/Reply: example with HTTP methods
- Remote Procedure Call (RPC) vs. Remote Method Invocation (RMI)
  - RMI is the OOP analog of RPC
  - Caller <-> Stub <-> Network <-> Skeleton (server stub) <-> Callee
- Interface Description Language (IDL): syntax for describing APIs for software component interactions.
  - Examples: OpenAPI, JSON, ProtoBuf, Apache Thrift.
- Popular API Architectures: MQTT, SOAP, GraphQL, WebHooks, REST, WebSocket.
- IP Multicast: sending to multiple receivers in a group.
- Overlay networks: virtual connections on top of an existing IP network.
- Super-peer architecture: P2P communication with special peers for easier coordination and management.

## Lecture 7 - Middleware for indirect Communication

- Indirect communication: for decoupling sender and receiver
  - State-based: shared memory
  - Message based
    - Group communication (e.g., Multicast)
    - Publish/Subscribe and Message Queues systems:
      - channel-based (e.g., receive only data from camera X)
      - topic-based (e.g., receive data  tagged with the "News" topic)
      - content-based (e.g., receive data in range {2, 5})
      - type-based (e.g., receive integer data only)

## Lecture 8 - Daemons and Distributed Objects

- Linux Daemons, services for common services like web, mail, logging, file sharing, ...
- [Distributed objects](https://en.wikipedia.org/wiki/Distributed_object): objects placed across different address spaces - now abandoned concept.
- [Common Object Request Broker Architecture (CORBA)](https://en.wikipedia.org/wiki/Common_Object_Request_Broker_Architecture): communication system - also abandoned
- [Jakarta Enterprise Beans (EJB)](https://en.wikipedia.org/wiki/Jakarta_Enterprise_Beans) - Java API for modular construction of software - abandoned
- [Wildfly](https://en.wikipedia.org/wiki/WildFly): application server - apparently still relevant!

## Lecture 9 - Client/Server Systems

![image-20241213185055187](/home/ahmed/.config/Typora/typora-user-images/image-20241213185055187.png)

- Client/server tiered architecture
  - Presentation tier
  - Application logic tier
  - Data management tier

- 2-tiered example: web browser and web server communicating over HTTP
- 3-tiered example: web browser, web server, and database server
- Web as a middleware for building distributed applications
  - Web services
  - Web mashup
  - SOAP, WADL, WSDL
  - REST

## Lecture 10 - Distributed File Systems

- Data Persistence

- SQL vs. NoSQL

- Distributed file systems vs. Distributed database

- Remote access vs. Upload/Download model

- Replication and Transparency

- Layered file system:

  - Programs -> logical file system -> file organization module -> basic file system -> I/O control -> devices

- UNIX file-system related system calls: open, create, close, read, write, lseek, link/unlink, stat.

- Distributed file system desired properties: transparency, scalability, fault tolerance

- Case studies:

  - Sun Network File System (NFS) & Andrew File System (AFS)

  ![image-20241213195513436](/home/ahmed/.config/Typora/typora-user-images/image-20241213195513436.png)

  - Hadoop Distributed File System (HDFS)
  - InterPlanetary File System (IPFS)

## Lecture 11 - Time in Distributed Systems

- Synchrony vs. asynchrony
- Physical vs. Logical clocks
- Clock skew
- Coordinated Universal Time (UTC)
- Clock synchronization using a time server
- Examples: Christan Algorithm, Network Time Protocol
- Lamport Logical Clocks
- Vector Clocks

## Lecture 12 - Distributed Consensus

- To achieve overall system reliability in the presence of a number of faulty processes or broken links
- CAP Theorem: consistency, availability, and partition tolerance: choose two
- Distributed mutual exclusion:
  - Types: token-based, non-token based, quorum-based
  - Requirements (for accessing critical section): safety, liveness, ordering
  - Example: ring algorithm
- Consensus algorithms: 
  - Requirements: termination, agreement, integrity
  - Byzantine generals problems
- Consistency models: strong, weak, eventual

## Lecture 13 - Security of Web-based Systems

- CIA Triad: Confidentiality, Integrity, Availability

- Security in distributed systems

  - Secure communication channels
  - Authorization mechanisms

- Different approaches focusing on: data, methods, or users.

- NIST cybersecurity framework

- Malware types

- Cryptography:

  - Symmetric and asymmetric key algorithms
  - Usage for confidentiality, integrity, authentication, and non-repudiation.

  

