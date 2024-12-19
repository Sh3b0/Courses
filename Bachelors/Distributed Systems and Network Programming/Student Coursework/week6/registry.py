import random
import threading
from xmlrpc.server import SimpleXMLRPCServer
from threading import Thread

REGISTRY_IP = 'localhost'
REGISTRY_PORT = 50000
VERBOSE = False
nodes = {}  # nodes[node_id] = port
random.seed(0)  # For deterministic testing


class Registry(Thread):
    def __init__(self, m):
        super().__init__()
        self.m = m

    def run(self):
        # Starts registry server
        if VERBOSE:
            print(f"Registry is running in {threading.currentThread().getName()}")
        with SimpleXMLRPCServer((REGISTRY_IP, REGISTRY_PORT), logRequests=False) as server:
            server.register_introspection_functions()
            server.register_instance(self)
            server.serve_forever()

    def register(self, port):
        # Returns a new id for the caller node (and network size), or an error.
        if len(nodes) == 2 ** self.m:
            return -1, f"Chord is full, network size = {len(nodes)}"
        node_id = random.randint(0, 2 ** self.m - 1)
        while node_id in nodes.keys():
            node_id = random.randint(0, 2 ** self.m - 1)
        nodes[node_id] = port
        if VERBOSE:
            print(f"Node {node_id} registered successfully, network size = {len(nodes)}")
        return node_id, len(nodes)

    def deregister(self, node_id):
        # Removes the node from the registry
        try:
            del nodes[node_id]
            if VERBOSE:
                print(f"Node {node_id} deregistered successfully.")
        except KeyError:
            return False, "No such id"
        return True, "Node deregistered successfully"

    def populate_finger_table(self, node_id):
        # FT_p[i]= succ(p+2^(i-1)) where i \in [1, m]
        ft = {}
        for i in range(1, self.m + 1):
            tmp = self.succ((node_id + 2 ** (i - 1)) % (2 ** self.m))
            if tmp != -1 and tmp != node_id:
                ft[str(tmp)] = nodes[tmp]
        if VERBOSE:
            print(f"FT for {node_id}: {ft}")
        return ft

    def get_chord_info(self):
        return nodes

    def succ(self, node_id):
        # Successor of node p is the next existing node in clockwise direction.
        for i in range(node_id, 2 ** self.m):
            if not nodes.get(i) is None:
                return i
        for i in range(0, node_id):
            if not nodes.get(i) is None:
                return i
        return -1
