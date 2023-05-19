---
marp: true
title: Week 5 Theory
---

# System Architecture

- **Centralized Architecture:** server(s) providing services and clients using them.
  - Application logic may be distributed (horizontally or vertically) over multiple machines (including the client machine).
- **Decentralized (Peer-to-Peer) Architecture:**
  - Each peer (node) may act as a server or a client (or both).
  - System functionality is horizontally distributed over multiple nodes.
- **Hybrid Architecture**
  - Examples include edge server systems (e.g., Hybrid CDNs) and collaborative distributed systems (e.g., BitTorrent).

---

# Structured vs. Unstructured P2P

- The P2P overlay network consists of all the participating peers as network nodes.
- **Unstructured P2P network** is formed when the overlay links are established arbitrarily.
  - To retrieve/distribute data among peers, a search algorithm (e.g., flooding or random walk) is used.
  - As the network grows, locating data items becomes inefficient.
- **Structured P2P networks** overcome the limitations of unstructured networks by maintaining a Distributed Hash Table (DHT).

---

# Distributed Hash Table (DHT)

- Each peer is assigned an identifier and will be responsible for holding a portion of the system data.
- To understand where a data item should reside (for insertion, updates or lookup), a consistent hash function is used to map a data item to a peer ID.
- DHT implementations include: [Chord](https://en.wikipedia.org/wiki/Chord_(peer-to-peer)), [Pastry](https://en.wikipedia.org/wiki/Pastry_(DHT)), [Tapestry](https://en.wikipedia.org/wiki/Tapestry_(DHT)), [Kademlia](https://en.wikipedia.org/wiki/Kademlia), [Bamboo](http://homepage.cs.uiowa.edu/~ghosh/Bamboo.pdf)

---

# Chord

- Nodes are logically organized in a ring; each node has an $m$ bit identifier and maintains a finger table.
- Finger table stores shortcut links between nodes that acheive faster (logarithmic) search time.
- Each data item is hashed to an $m$ bit key. Data item with key $k$ is stored at the successor node of $k$.
- Chord algorithm specifies how to calculate the successor node for a given key.
- When a new node joins or exits the chord, special preocedures are executed to update the system state and ensure all nodes are up-to-date with the topology.
