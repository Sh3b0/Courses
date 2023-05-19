from argparse import ArgumentParser
from bisect import bisect_left
from xmlrpc.client import ServerProxy
from xmlrpc.server import SimpleXMLRPCServer

M = 5
PORT = 1234
RING = [2, 7, 11, 17, 22, 27]


class Node:
    def __init__(self, node_id):
        """Initializes the node properties and constructs the finger table according to the Chord formula"""
        self.node_id = node_id
        self.pred_id = RING[RING.index(self.node_id) - 1]
        self.finger_table = []
        self.data = {}

        for i in range(M):
            idx = bisect_left(RING, (self.node_id + 2 ** i) % (2 ** M))
            if idx == len(RING):
                idx = 0
            self.finger_table.append(RING[idx])

        self.finger_table = list(self.finger_table)
        print(f"Node created! Finger table = {self.finger_table}")

    def closest_preceding_node(self, id):
        """Returns node_id of the closest preceeding node (from n.finger_table) for a given id"""
        l, r = self.node_id, id
        if l > r:
            ranges = [range(l+1, 2**M), range(0, r)]
        else:
            ranges = [range(l+1, r), []]
        for finger in reversed(self.finger_table):
            if finger in ranges[0] or finger in ranges[1]:
                return finger
        return self.node_id

    def find_successor(self, id):
        """Recursive function returning the identifier of the node responsible for a given id"""
        if id == self.node_id:
            return id
        l, r = self.node_id, self.finger_table[0]
        if l > r:
            ranges = [range(l+1, 2**M), range(0, r+1)]
        else:
            ranges = [range(l+1, r+1), []]
        if id in ranges[0] or id in ranges[1]:
            return r
        n0 = self.closest_preceding_node(id)
        print(f"Forwarding request (key={id}) to node {n0}")
        with ServerProxy(f'http://node_{n0}:{PORT}') as node:
            return node.find_successor(id)

    def put(self, key, value):
        """Stores the given key-value pair in the node responsible for it"""
        print(f"put({key}, {value})")
        target_node = self.find_successor(key)
        print(f"Forwarding request (key={key}) to node {target_node}")
        if self.node_id == target_node:
            return self.store_item(key, value)
        with ServerProxy(f'http://node_{target_node}:{PORT}') as node:
            return node.store_item(key, value)

    def get(self, key):
        """Gets the value for a given key from the node responsible for it"""
        print(f"get({key})")
        target_node = self.find_successor(key)
        if self.node_id == target_node:
            return self.retrieve_item(key)
        print(f"Forwarding request (key={key}) to node {target_node}")
        with ServerProxy(f'http://node_{target_node}:{PORT}') as node:
            return node.retrieve_item(key)

    def store_item(self, key, value):
        """Stores a key-value pair into the data store of this node"""
        self.data[key] = value
        return True

    def retrieve_item(self, key):
        """Retrieves a value for a given key from the data store of this node"""
        return self.data.get(key, -1)


if __name__ == '__main__':
    parser = ArgumentParser()
    parser.add_argument('node_id', type=int)
    args = parser.parse_args()
    with SimpleXMLRPCServer(('0.0.0.0', PORT), logRequests=False) as server:
        server.register_introspection_functions()
        server.register_instance(Node(args.node_id))
        try:
            server.serve_forever()
        except KeyboardInterrupt:
            print("Shutting down...")
