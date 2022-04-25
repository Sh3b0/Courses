import threading
import time
from threading import Thread
from threading import Lock
from xmlrpc.client import ServerProxy

REGISTRY_IP = 'localhost'
REGISTRY_PORT = 50000
REGISTRY = ServerProxy(f'http://{REGISTRY_IP}:{REGISTRY_PORT}')
VERBOSE = False
progress = 0
lock = Lock()


class Node(Thread):
    def __init__(self, port):
        super().__init__()
        self.port = port
        self.node_id = -1
        self.ft = {}

    def run(self):
        # Registers node (self) and populates its finger table through thread-safe RPC calls to the registry
        global progress
        with lock:
            node_id, msg = REGISTRY.register(self.port)
            if node_id == -1:
                if VERBOSE:
                    print("Can't create node: " + msg)
                progress += 1
                return
            self.node_id = node_id
        if VERBOSE:
            print(f"Node {self.node_id} is running in {threading.currentThread().getName()}")
        time.sleep(1)
        with lock:
            self.ft = REGISTRY.populate_finger_table(self.node_id)
            progress += 1

    def quit(self):
        # Deregisters node (self) through thread-safe RPC call to the registry
        with lock:
            success, msg = REGISTRY.derigester(self.node_id)
            if not success:
                print("Can't deregister node: " + msg)
                return
        if VERBOSE:
            print(f"Node {self.node_id} quit.")

    def get_finger_table(self):
        return self.ft
