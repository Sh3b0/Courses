import random
import threading
import time
from argparse import ArgumentParser
from xmlrpc.server import SimpleXMLRPCServer
from xmlrpc.client import ServerProxy
import socket  # Just for setting global timeout
from enum import Enum
import os

DEBUG_MODE = False
servers = {}

socket.setdefaulttimeout(0.04)


class State(Enum):
    FOLLOWER = 0
    CANDIDATE = 1
    LEADER = 2


class Server:
    def __init__(self, server_id):
        self.id = server_id
        self.leader_id = -1
        self.term = 0
        self.state = State.FOLLOWER
        self.vote_count = 0
        self.has_voted_in_term = {}
        self.timer = threading.Timer(random.randint(150, 300) / 1000, self.timeout)
        self.timer.start()
        self.suspended = False

        with SimpleXMLRPCServer((servers[self.id][0], servers[self.id][1]), logRequests=False) as server:
            server.register_instance(self)
            print(f'server is started at {servers[self.id][0]}:{servers[self.id][1]}')
            print(f'I am a follower. Term: 0')
            server.serve_forever()

    def request_vote(self, term, candidate_id):
        # Returns term number of the server and result of voting
        if DEBUG_MODE:
            print(f'Candidate {candidate_id} asked for my vote with term {term}')
        if self.state == State.FOLLOWER:
            self.timer.cancel()
            self.start_timer()
        elif self.state == State.LEADER:
            return self.term, False

        if term == self.term and not self.has_voted_in_term.get(term, False):
            self.has_voted_in_term[term] = True
            print(f'Voted for node {candidate_id}')
            return self.term, True
        elif term > self.term:
            self.term = term
            self.has_voted_in_term[term] = True
            print(f'Voted for node {candidate_id}')
            return self.term, True

        return self.term, False

    def start_timer(self):
        self.timer = threading.Timer(random.randint(150, 300) / 1000, self.timeout)
        self.timer.start()

    def append_entries(self, term, leader_id):
        if self.state == State.FOLLOWER:
            self.timer.cancel()
            self.start_timer()
        self.leader_id = leader_id
        return self.term, term >= self.term

    def get_leader(self):
        print("Command from client: getleader")
        result = f'{self.leader_id} {servers[self.leader_id][0]}:{servers[self.leader_id][1]}'
        print(result)
        return result

    def suspend_util(self, period):
        print(f"Command from client: suspend {period}")
        print(f"Sleeping for {period} seconds.")
        self.suspended = True
        self.timer.cancel()
        time.sleep(period)
        self.suspended = False
        self.start_timer()

    def suspend(self, period):
        t = threading.Thread(target=self.suspend_util, args=(period,))
        t.start()
        return True

    def timeout(self):
        if self.state == State.LEADER:
            self.start_timer()
            return

        print(f"The leader is dead")
        if DEBUG_MODE:
            print(time.time())
        print(f'I am a candidate. Term: {self.term}')
        self.state = State.CANDIDATE
        self.term += 1
        self.vote_count = 1
        self.has_voted_in_term[self.term] = True
        print(f'Voted for node {self.id} (self)')

        alive_servers = len(servers)
        for sid, (ip, port) in servers.items():
            if sid == self.id:
                continue
            try:
                with ServerProxy(f'http://{ip}:{port}') as server:
                    if server.request_vote(self.term, self.id)[1]:
                        self.vote_count += 1
            except ConnectionRefusedError:
                alive_servers -= 1  # Peer not available
            except socket.timeout:
                alive_servers -= 1  # Peer is suspended
        print(f'Votes received ({self.vote_count}/{alive_servers})')
        if self.vote_count > len(servers) // 2 or alive_servers <= len(servers) // 2:
            self.state = State.LEADER
            self.leader_id = self.id
            print(f'I am a leader. Term: {self.term}')
            self.vote_count = 0
            while self.state == State.LEADER and not self.suspended:
                for sid, (ip, port) in servers.items():
                    try:
                        with ServerProxy(f'http://{ip}:{port}') as server:
                            if not server.append_entries(self.term, self.id)[1]:
                                self.state = State.FOLLOWER
                    except ConnectionRefusedError:
                        pass  # Peer not available
                    except socket.timeout:
                        pass  # Peer is suspended
                time.sleep(0.05)
        else:
            self.state = State.FOLLOWER

        self.start_timer()

    def ping(self):
        return True


if __name__ == '__main__':
    parser = ArgumentParser()
    parser.add_argument('id', type=int, help='server id to run')
    args = parser.parse_args()

    try:
        with open('config.conf') as f:
            lines = [line for line in f.readlines()]
            for line in lines:
                tmp = line.split()
                servers[int(tmp[0])] = (tmp[1], int(tmp[2]))
    except ValueError:
        print("Invalid config.conf file")
        exit()
    except FileNotFoundError:
        print('config.conf file not found')
        exit()

    try:
        Server(args.id)
    except OSError:
        print("Address already in use.")
        os._exit(0)
    except KeyboardInterrupt:
        os._exit(0)
