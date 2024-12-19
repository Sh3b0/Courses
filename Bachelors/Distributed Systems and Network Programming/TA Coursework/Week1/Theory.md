---
marp: true
title: Week 1 Theory
---

# **Distributed System**

A collection of autonomous computing elements (nodes) that appears to its users as a single coherent system. They communicate via a network (by passing messages) and collaborate in achieving tasks.

- Distribution refers to differences of location.
- Centralization and decentralization refer to different levels of control.

---

# **Topics of interest**

- Concurrency and Parallelism
  - Multithreading, multiprocessing, asynchronous programming.
- Synchronization and Coordination
  - Clock synchronization, election, mutual exclusion.
- System Architecture
  - Centralized vs. Peer-to-peer vs. hybrid architecture.
- Consistency and Replication
  - Replica management, consistency protocols.

---

# **Lab Topics (tentative)**

1. Socket Programming in Python - Stop-and-Wait ARQ
2. Multithreading, Multiprocessing, and AsyncIO in Python
3. Message Queuing - ZeroMQ
4. Remote Procedure Call - gRPC
5. Distributed Hash Table - Chord Protocol
6. Consensus Algorithms - RAFT

---

# **Distributed systems may provide better:**

- **Scalability:** the extent to which the system can expand (handle more users) without failures.
  - Size-scalability, geographical scalability, and administrative scalability.
- **Availability:** uptime / (uptime + downtime).
- **Reliability:** the probability of system to work without disruption during some time interval.
  - Fault tolerance mechanisms aim to detect and recover from failures with minimal downtime.
  - Metrics including MTBF and MTTR are used to measure reliability and fault tolerance.
- **Performance:** optimizing metrics such as latency, throughput, and resource utilization.

---

# **Distributed systems design challenges**

- **Resource limitations:** processing, memory, and storage.
- **Lack of global clock:** for coordination and synchronization.
- **Network partitioning:** part of the system becomes unreachable.
- **Concurrency failures:** non-determinism and race conditions.
- **Security:** ensuring data confidentiality, integrity, and availability.

---

# **Automatic Repeat Query (ARQ)**

An error-control method for data transmission that uses acknowledgements and timeouts to achieve reliable data transmission over an unreliable communication channel.

- **Acknowledgements:** messages sent by the receiver indicating that it has correctly received a message.
- **Timeout:** a specified periods of time allowed to elapse before an acknowledgment is to be received.
