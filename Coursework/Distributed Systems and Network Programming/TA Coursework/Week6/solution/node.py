import random
import sched
import socket
import time
from threading import Thread
from argparse import ArgumentParser
from enum import Enum
from xmlrpc.client import ServerProxy
from xmlrpc.server import SimpleXMLRPCServer

PORT = 1234
CLUSTER = [1, 2, 3]
ELECTION_TIMEOUT = (6, 8)
HEARTBEAT_INTERVAL = 5


class NodeState(Enum):
    """Enum listing the three possible node states (follower, candidate, or leader)"""
    FOLLOWER = 1
    CANDIDATE = 2
    LEADER = 3


class Node:
    def __init__(self, node_id):
        """Non-blocking procedure to initialize all node parameters and start the first election timer"""
        self.node_id = node_id
        self.state = NodeState.FOLLOWER
        self.term = 0
        self.votes = {}
        self.log = []
        self.pending_entry = ''
        self.sched = sched.scheduler()
        self.election_event = self.sched.enter(
            delay=random.uniform(*ELECTION_TIMEOUT),
            priority=2,
            action=self.hold_election
        )
        print(f"Node started! State: {self.state}. Term: {self.term}")

    def is_leader(self):
        """Returns True if this node is the elected cluster leader and False otherwise"""
        return self.state == NodeState.LEADER

    def reset_election_timer(self):
        """Resets election timer for this (follower or candidate) node and returns it to the follower state"""
        try:
            self.sched.cancel(self.election_event)
        except ValueError:
            self.state = NodeState.FOLLOWER
        self.election_event = self.sched.enter(
            delay=random.uniform(*ELECTION_TIMEOUT),
            priority=2,
            action=self.hold_election
        )

    def hold_election(self):
        """Called when this follower node is done waiting for a message from a leader (election timeout)
            The node increments term number, becomes a candidate and votes for itself.
            Then call request_vote over RPC for all other online nodes and collects their votes.
            If the node gets the majority of votes, it becomes a leader and start the hearbeat timer
            If the node loses the election, it returns to the follower state and resets election timer.
        """
        self.term += 1
        self.state = NodeState.CANDIDATE
        vote_count = 1
        self.votes[self.term] = self.node_id
        print(f"New election term {self.term}. State: Candidate")
        for node_id in CLUSTER:
            if node_id != self.node_id:
                with ServerProxy(f'http://node_{node_id}:{PORT}') as node:
                    print(f"Requesting vote from node {node_id}")
                    try:
                        if node.request_vote(self.term, self.node_id):
                            vote_count += 1
                    except socket.error:
                        print(f"Node {node_id} is offline")
        if vote_count > len(CLUSTER) // 2:
            print(f"Received {vote_count} votes. State: Leader")
            self.state = NodeState.LEADER
            self.heartbeat_event = self.sched.enter(
                delay=HEARTBEAT_INTERVAL,
                priority=1,
                action=self.append_entries
            )
        else:
            print(f"Received {vote_count} votes. State: Follower")
            self.state = NodeState.FOLLOWER
            self.reset_election_timer()

    def request_vote(self, term, candidate_id):
        """Called remotely when a node requests voting from other nodes.
            Updates the term number if the received one is greater than `self.term`
            A node rejects the vote request if it's a leader or it already voted in this term.
            Returns True and update `self.votes` if the vote is granted to the requester candidate and False otherwise.
        """
        print(f"Got a vote request from {candidate_id}")
        if self.state == NodeState.LEADER:
            print(f"Didn't vote for {candidate_id} (I'm a leader)")
            return False
        if term > self.term:
            self.term = term
        if self.votes.get(term) is None:
            print(f"Voted for {candidate_id}. Term: {term}")
            self.votes[term] = candidate_id
            self.reset_election_timer()
            return True
        print(
            f"Didn't vote for {candidate_id} (already voted for {self.votes[term]})")
        return False

    def append_entries(self):
        """Called by leader every HEARTBEAT_INTERVAL, sends a heartbeat message over RPC to all online followers.
            Accumulates ACKs from followers for a pending log entry (if any)
            If the majority of followers ACKed the entry, the entry is committed to the log and is no longer pending
        """
        self.heartbeat_event = self.sched.enter(
            delay=HEARTBEAT_INTERVAL,
            priority=1,
            action=self.append_entries
        )
        print("Sending a heartbeat to followers")
        ack_count = 1
        for node_id in CLUSTER:
            if node_id != self.node_id:
                try:
                    with ServerProxy(f'http://node_{node_id}:{PORT}') as node:
                        if node.heartbeat(self.pending_entry):
                            ack_count += 1
                except socket.error:
                    print(f"Follower node {node_id} is offline")
        if self.pending_entry == '':
            return
        if ack_count > len(CLUSTER) // 2:
            print(f"Leader committed '{self.pending_entry}'")
            self.log.append(self.pending_entry)
            self.pending_entry = ''

    def heartbeat(self, leader_entry):
        """Called remotely from the leader to inform followers that it's alive and inform them if there is any pending log entry
            Followers would commit an entry if it was pending before, but is no longer now.
            Returns True to ACK the heartbeat and False on any problems.
        """
        print(f"Heartbeat received from leader (entry='{leader_entry}')")
        self.reset_election_timer()
        if self.pending_entry != '' and leader_entry == '':
            print(f"Follower committed '{self.pending_entry}'")
            self.log.append(self.pending_entry)
        self.pending_entry = leader_entry
        return True

    def leader_receive_log(self, log):
        """Called remotely from the client. Executed only by the leader upon receiving a new log entry
            Returns True after the entry is committed to the leader log and False on any problems
        """
        print(f"Leader received log '{log}' from client")
        self.pending_entry = log
        while (self.pending_entry != ''):
            time.sleep(1)
        return True


if __name__ == '__main__':
    parser = ArgumentParser()
    parser.add_argument('node_id', type=int)
    args = parser.parse_args()

    with SimpleXMLRPCServer(('0.0.0.0', PORT), logRequests=False) as server:
        server.register_introspection_functions()
        n = Node(args.node_id)
        server.register_instance(n)
        Thread(target=n.sched.run).start()
        try:
            server.serve_forever()
        except KeyboardInterrupt:
            for event in n.sched.queue:
                n.sched.cancel(event)
            print("Shutting down...")
