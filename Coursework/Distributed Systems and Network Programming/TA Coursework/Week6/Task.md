# Week 6 - Distributed Consensus

> Distributed Systems and Network Programming - Spring 2023

## Overview

Your task for this lab is to use [RAFT](https://en.wikipedia.org/wiki/Raft_(algorithm)) algorithm to maintain consensus between 3 nodes in a P2P system. All nodes in the cluster need to maintain a consistent replicated log (list of strings).

## Brief Algorithm Description

RAFT works in two phases: **leader election** and **log replication**.

- Check [this](https://thesecretlivesofdata.com/raft) visualization for a better understanding.

### Leader Election

- In this phase, nodes must select a leader to be the single trusted source of information.
- Leader election algorithm should be fault tolerant.
  - The system should stay operational as nodes go online or offline.
  - If a leader goes offline, a new leader should be elected.

- This is implemented using node states, election and heartbeat timers, and election term.

### Log Replication

- In this phase, a client sends updates (log entries) to the leader.
- The leader should propagate this information to online followers.
- All online nodes in the cluster should maintain the same log.

## Task

- Implement a RAFT node to communicate with the given client.
- You may use the given boilerplate code or write your own.
  - Logging that shows all major events in the cluster is required for grading.
  - Check the output below for reference, similar logging is required.

- Use larger timeouts for easier inspection of the output
  - `ELECTION_TIMEOUT`: chosen uniformly between 6 and 10 seconds.
  - `HEARTBEAT_INTERVAL`: 2 seconds.

## Example Run

### Input

- We have prepared an example cluster of 3 nodes (numbered from 1 to 3) and the required docker files to run all such nodes in one network, nodes are reachable from each other by their hostname (e.g., `http://node_1:1234`)

- An example client is also provided (`client.py`). The client runs in the same network with nodes and contacts the leader node (after leader election is done) to insert some data into the log.

- To run the example, execute the following command in the project directory

  ```bash
  docker-compose build --no-cache && docker-compose up
  ```

### Output

#### Typical Run

- No interruptions or split-votes occur. Log replication happens normally.

![typical_run](https://i.imgur.com/vHSO5KT.png)

#### Split-vote case

- Nodes 1 and 3 hold elections simultaneously
- Only one of them should win and the elections should stop occurring

![split_vote](https://i.imgur.com/rnU4S2i.png)

#### Dead leader

- Ran `docker container stop node_3` to stop the leader
- Election happened and `node_1` became the new leader
- Ran `docker container start node_3` to revive the dead leader
- Old leader is now a follower and receives heartbeats from `node_1`

![img](https://i.imgur.com/z7lTJ2U.png)

#### Dead follower

- Same as the above case, but stopping a follower (`node_1`) instead of the leader.

- Election may happen, but leader should not change.

- When the follower rejoins, everything goes back to normal.

  ![dead_follower](https://i.imgur.com/tyBdpwU.png)

## Checklist

- [ ] A single submitted file (`node_NameSurname.py`)
- [ ] The code is readable and does not use any external dependencies
- [ ] Logging shows all major system events in detail (check example run above)
- [ ] Leader election and log replication are correctly implemented (depends on the above point)
- [ ] The system is resilient to random testing (nodes may go online or offline any time)
- [ ] The source code is the author's original work. Both parties will be penalized for detected plagiarism

## Additional Notes

- Original RAFT paper and a list of popular implementations are available [here](https://raft.github.io/)
- The following simplifications were made to adjust the complexity of the task:
  - The number of nodes in the cluster is fixed to 3.
  - Nodes may go online or offline any time, except while logs are being propagated.
  - A revived node does not need to re-obtain historical logs.
  - Client logic is separated from node logic for easier testing.
  - The network is assumed to be reliable and network partitioning will not happen.
