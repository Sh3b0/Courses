# Week 5 - Distributed Hash Tables

> Distributed Systems and Network Programming - Spring 2023

## Overview

Your task for this lab is to implement a simplified version of the [Chord](https://en.wikipedia.org/wiki/Chord_(peer-to-peer)) algorithm used to maintain a Distributed Hash Table (DHT) in peer-to-peer systems

- DHT is a regular dictionary (HashMap) in which entries (i.e., keys and their corresponding values) are distributed over multiple nodes (peers) based on the consistent hash of the key
- Nodes only support the two basic hash table operations: `get(key)` and `put(key, value)`

## System Architecture

Chord operates over a structured P2P overlay network in which nodes (peers) are organized in a ring

- Each node has an integer identifier: $n \in [0, 2^{M})$, where $M$ is the key-length in bits
- Total number of nodes: $N \leq 2^M$
- Each node should stay up-to-date about the current topology of the ring
- Nodes communicate over the network using RPC

### Node

- Each node is responsible for storing values for keys in the range `(predecessor_id, node_id]` except the first node which stores values for keys in the range `(predecessor_id, 2**M) U [0, node_id]`

- Each node maintains a **finger table** (i.e., a list of identifiers for some other nodes)
  - A finger table contains $M$ entries (repetitions are allowed)
  - The value of the $i^{th}$ element in the finger table for node $n$ is defined as follows:
    
    $$
    FT_{n}[i] = successor((n + 2^{i}) \mod 2^M), i \in \{0..M-1\}
    $$
  - Successor function returns the identifier of the next online node in the ring (clockwise direction).
  - Finger tables are used by the Chord algorithm to achieve a logarithmic search time. They are the reason behind Chord scalability.

- Chord algorithm relies on two functions (`find_successor` and `closest_preceding_node`) to determine in which node should an entry (key-value pair) reside. The pseudo-code for these functions is given below:

  ```python
  # Recursive function returning the identifier of the node responsible for a given id
  n.find_successor(id):
      if id == n.id:
        return id
  
      # Note the half-open interval and that L <= R does not necessarily hold
      if id ∈ (n.id, n.successor_id] then
          return n.successor_id
       
      # Forward the query to the closest preceding node in the finger table for n
      n0 := closest_preceding_node(id)
      return n0.find_successor(id)
  ```

  ```python
  # Returns the closest preceeding node (from n.finger_table) for a given id
  n.closest_preceding_node(id):
      # Note the full-open interval and that L <= R does not necessarily hold
      for i = m downto 1 do
          if finger[i].id ∈ (n.id, id) then
              return finger[i]
      return n
  ```

### Client

- The client provided uses [xmlrpc](https://docs.python.org/3/library/xmlrpc.html) to:
  - Call a random node over RPC, the node should be ready and listening
  - Ask the node to insert an entry into the chord
  - Then execute some lookup operations
    - Lookup operation asks a certain node about the value for a certain key

- For the node to answer a request, it may contact other nodes as explained above
  - **For `put` queries:** return `True` on successful insertion and `False` otherwise
  - **For `get` queries:**
    - If the value for the given key is found, return that value
    - If the value is not found, return `-1`

## Task

- Implement the Node class as explained above. The boilerplate is given in `node.py`
  - Parse one integer argument (`node_id`)
  - Create a Node instance and register its methods for calling over XML-RPC
  - Run XML-RPC server on <http://0.0.0.0:1234>
  - Write implementation for `find_successor` and `closest_preceding_node` according to the provided pseudo-code
  - Write the logic for `n.get(key)` and `n.put(key, value)` to insert and retrieve data from the Chord
- Test your code using docker as explained below. **Code which does not work will receive 0 grade**
- Submit a single file `node_NameSurname.py`

## Example Run

### Input

- We have prepared an example ring of 6 nodes and the required docker files to run all such nodes in one network, nodes are reachable from each other by their hostname (e.g., `http://node_22:1234`)

- An example client is also provided (`client.py`). The client runs in the same network with nodes and contacts random nodes from the ring, asking them to insert data into the Chord
  - Overall, the client inserts 32 key-value pairs (i.e., `(0, "value_0")` up to `(31, "value_31")`)

- To run the example, execute the following command in the project directory

  ```bash
  docker-compose build --no-cache && docker-compose up
  ```

- Note that your code will be tested on different rings, a correct implementation should always work

### Output

- The output shows how lookup queries are routed through the ring, the routing order is deterministic.
- For simplicity, the Chord does not have any data stored in this run (that is why `-1` is returned)
- The `client.py` provided should result in more output as `put` requests are routed between nodes. `get` requests should then return the expected values (i.e., `lookup(N, X) = "value_X"`)

    ![output](https://i.imgur.com/oQwlFla.png)

### Visualization

- You can use [Chordgen](https://msindwan.github.io/chordgen/) to visualize test cases and verify finger tables and lookup order
- The following diagram shows the given example ring and data allocation
     ![example](https://i.imgur.com/WS4JHjn.png)

## Checklist

- [ ] A single submitted file (`node_NameSurname.py`)
- [ ] The code is formatted and does not use any external dependencies
- [ ] Logging shows the finger table for each node
- [ ] Logging shows which node functions were called over RPC and how the request was routed (see example output above)
- [ ] The system provides the correct output for **the given example** ring and lookup queries
- [ ] The system provides the correct output for **a different** ring and lookup queries
- [ ] The source code is the author's original work. Both parties will be penalized for detected plagiarism

## Additional Notes

- Chord is quite a complex protocol, you can find the original implementation [here](https://github.com/sit/dht)
- The following simplifications were made to adjust the complexity of the task:
  - Each node is initialized with knowledge about other nodes in the ring, removing the need to implement notification procedures
  - The system topology is fixed, removing the need to implement procedures for stabilization, node joining/exiting and finger table updates
  - A client is provided to test the system. In real-world scenarios, that client is typically a node as well
- In real-world implementations, the Chord and its finger tables should update dynamically as nodes enter and exit the system. Periodical stabilization procedures are also used to ensure that nodes stay up-to-date with the current topology of the ring
