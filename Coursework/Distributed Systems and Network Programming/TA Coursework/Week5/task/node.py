from argparse import ArgumentParser
from bisect import bisect_left
from threading import Thread
from xmlrpc.client import ServerProxy
from xmlrpc.server import SimpleXMLRPCServer

M = 5
PORT = 1234
RING = [2, 7, 11, 17, 22, 27]


class Node:
    def __init__(self, node_id):
        """Initializes the node properties and constructs the finger table according to the Chord formula"""
        self.finger_table = []
        print(f"Node created! Finger table = {self.finger_table}")

    def closest_preceding_node(self, id):
        """Returns node_id of the closest preceeding node (from n.finger_table) for a given id"""
        return -1

    def find_successor(self, id):
        """Recursive function returning the identifier of the node responsible for a given id"""
        return -1

    def put(self, key, value):
        """Stores the given key-value pair in the node responsible for it"""
        print(f"put({key}, {value})")
        return True

    def get(self, key):
        """Gets the value for a given key from the node responsible for it"""
        print(f"get({key})")
        return -1

    def store_item(self, key, value):
        """Stores a key-value pair into the data store of this node"""
        return True

    def retrieve_item(self, key):
        """Retrieves a value for a given key from the data store of this node"""
        return -1


if __name__ == '__main__':
    pass
