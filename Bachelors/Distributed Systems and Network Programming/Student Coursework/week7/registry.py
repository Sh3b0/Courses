import random
import threading
import time
from xmlrpc.server import SimpleXMLRPCServer
from xmlrpc.client import ServerProxy
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
        """
        Starts registry server
        """
        if VERBOSE:
            print(f"Registry is running in {threading.currentThread().getName()}")
        with SimpleXMLRPCServer((REGISTRY_IP, REGISTRY_PORT), logRequests=False) as server:
            server.register_introspection_functions()
            server.register_instance(self)
            server.serve_forever()

    def register(self, port):
        """
        Returns a new id for the caller node (and network size), or an error.
        """
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
        """
        Removes the node from the registry
        :return: (success, msg)
        """
        try:
            nodes.pop(node_id)
        except KeyError:
            return False, "No such id"

        for nid, port in nodes.items():
            with ServerProxy(f'http://localhost:{port}') as node:
                new_ft = self.populate_finger_table(nid)[0]
                if VERBOSE:
                    print(f"New FT for {nid} after {node_id} quit is: ", new_ft)
                node.set_finger_table(new_ft)
        if VERBOSE:
            print(f"Node {node_id} deregistered successfully.")

        return True, "Node deregistered successfully"

    def populate_finger_table(self, node_id):
        """
        :returns: Finger table of the calling node, and it's predecessor id, port
        """
        # FT_p[i]= succ(p+2^(i-1)) where i \in [1, m]
        ft = {}
        for i in range(1, self.m + 1):
            tmp = self.succ((node_id + 2 ** (i - 1)) % (2 ** self.m))
            if tmp != -1 and tmp != node_id:
                ft[str(tmp)] = nodes[tmp]  # Can't send a dict with int keys through RPC, will cast back in node.
        if VERBOSE:
            print(f"FT for {node_id}: {ft}")
        pred_id = self.pred(node_id)
        return ft, (pred_id, nodes[pred_id])

    def get_chord_info(self):
        """
        :returns: Dictionary of existing nodes in chord and their corresponding ports
        """
        return nodes

    def succ(self, node_id):
        """
        returns successor of node node_id which is the next existing node in clockwise direction.
        """
        for i in range(node_id, 2 ** self.m):
            if not nodes.get(i) is None:
                return i
        for i in range(0, node_id):
            if not nodes.get(i) is None:
                return i
        return -1

    def pred(self, node_id):
        """
        Returns predecessor of node node_id which is the next existing node in counter clockwise direction.
        """
        if node_id == 0:
            node_id = 2 ** self.m - 1
        else:
            node_id -= 1
        for i in range(node_id, -1, -1):
            if not nodes.get(i) is None:
                return i
        for i in range(2 ** self.m - 1, node_id, -1):
            if not nodes.get(i) is None:
                return i
        return -1
