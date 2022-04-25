import threading
import time
from threading import Thread
from threading import Lock
from xmlrpc.server import SimpleXMLRPCServer
from xmlrpc.client import ServerProxy

REGISTRY_IP = 'localhost'
REGISTRY_PORT = 50000
REGISTRY = ServerProxy(f'http://{REGISTRY_IP}:{REGISTRY_PORT}')
VERBOSE = False
progress = 0
lock = Lock()


class Node(Thread):
    def __init__(self, port, m):
        super().__init__()

        # All nodes were assumed to run on localhost
        # If it's not the case, finger tables has to be
        # extended to store node ip along with port
        # and recursive calls to other nodes has to be modified accordingly
        self.ip = 'localhost'

        self.port = port
        self.id = -1
        self.pred = (-1, -1)  # node_id, port
        self.succ = (-1, -1)
        self.ft = {}
        self.m = m
        self.files = []

    def run(self):
        """
        1. Register node (self), node_id and port are assigned.
        2. Populates node finger table, and assign predecessor
        3. runs node RPC server on {self.ip:self.port}
            * Communication with Registry is done through thread-safe RPC calls
        """
        global progress
        with lock:
            node_id, msg = REGISTRY.register(self.port)
            if node_id == -1:
                if VERBOSE:
                    print("Can't create node: " + msg)
                progress += 1
                return
            self.id = node_id
        time.sleep(1)
        with lock:
            tmp, self.pred = REGISTRY.populate_finger_table(self.id)
            for i in tmp.items():
                self.ft[int(i[0])] = i[1]
            self.succ = list(self.ft)[0], self.ft[list(self.ft)[0]]
            progress += 1
        if VERBOSE:
            print(f"Node {self.id} is running in {threading.currentThread().getName()}")
            print(f"Node {self.id} is accessible through http://localhost:{self.port}")
            print(f"Node {self.id} predecessor is {self.pred}")
        with SimpleXMLRPCServer((self.ip, self.port), logRequests=False) as server:
            server.register_introspection_functions()
            server.register_instance(self)
            server.serve_forever()

    def file(self, target_id, filename, op='save'):
        """
        Saves/Gets a file from one of the nodes, depending on filename hash.

        :param target_id: hash_value of filename, used to find the node responsible for file
        :param op: operation to do on file, can be 'get' or 'save'
        :param filename: the filename to be saved/retrieved
        :return: (success, msg)
        """
        if VERBOSE:
            print(f'Node {self.id} is asked to {op} file with id {target_id}')
        if self.pred[0] > self.id:
            range1 = range(0, self.id + 1)
        else:
            range1 = range(self.pred[0] + 1, self.id + 1)
        if self.id > self.succ[0]:
            range2 = range(0, self.succ[0] + 1)
        else:
            range2 = range(self.id + 1, self.succ[0] + 1)
        if target_id in range1:
            if VERBOSE:
                print(f'{target_id} is in {range1}')
            return self.file_util(op, filename)
        elif target_id in range2:
            if VERBOSE:
                print(f'{target_id} is in {range2}')
            if op == 'get':
                print(f'Node {self.id} passed request to node {self.succ[0]}')
            elif op == 'save':
                print(f'Node {self.id} passed {filename} to node {self.succ[0]}')
            with ServerProxy(f'http://localhost:{self.ft[self.succ[0]]}') as node:
                return node.file(target_id, filename, op)
        else:
            if VERBOSE:
                print(f'{target_id} doesn\'t belong to node {self.id} or it\'s successor {self.succ[0]}')
            latest_id, latest_port = -1, -1
            for node_id, node_port in self.ft.items():
                if target_id <= node_id:
                    latest_id, latest_port = node_id, node_port
            if latest_id == -1:  # This node is the only one in chord
                return self.file_util(op, filename)

            if op == 'get':
                print(f'Node {self.id} passed request to node {latest_id}')
            elif op == 'save':
                print(f'Node {self.id} passed {filename} to node {latest_id}')
            with ServerProxy(f'http://localhost:{latest_port}') as node:
                return node.file(target_id, filename, op)

    def file_util(self, op, filename):
        if filename in self.files:
            if op == 'get':
                return [True, f'Node {self.id} has {filename}']
            elif op == 'save':
                return [False, f'{filename} already exists in node {self.id}']
        else:
            if op == 'get':
                return [False, f'Node {self.id} doesn\'t have {filename}']
            elif op == 'save':
                self.files.append(filename)
                return [True, f'{filename} is saved in node {self.id}']

    def quit(self):
        """
        Fixes the chord by
          1. changing predecessor of successor node
          2. changing successor of predecessor node
          3. transferring all files to successor node
          4. updating all finger tables (done by registry)
        then deregister node (self) through RPC call to the registry
        :return: (success, msg)
        """
        with lock:
            success, msg = REGISTRY.deregister(self.id)
            if not success:
                return False, f'Node {self.id} with port {self.port} isn\'t part of the network'
            with ServerProxy(f'http://localhost:{self.succ[1]}') as node:
                node.set_pred(self.pred)
                if self.files:
                    node.append_files(self.files)
            with ServerProxy(f'http://localhost:{self.pred[1]}') as node:
                node.set_succ(self.succ)
            return True, f'Node {self.id} with port {self.port} was successfully removed'

    def get_finger_table(self):
        """
        :returns: finger table of self
        """
        return self.ft

    def set_finger_table(self, new_ft):
        """
        Sets finger_table of self to a new one.
        """
        self.ft = {}
        for i in new_ft.items():
            self.ft[int(i[0])] = i[1]
        if VERBOSE:
            print(f'ft for {self.id} set successfully to {self.ft}')
        return True

    def append_files(self, new_files):
        """
        Appends a list of files to self.
        """
        self.files += new_files
        return True

    def set_pred(self, new_pred):
        self.pred = new_pred
        return True

    def set_succ(self, new_succ):
        self.succ = new_succ
        return True
