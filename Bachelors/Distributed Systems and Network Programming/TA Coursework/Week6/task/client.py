from xmlrpc.client import ServerProxy
import socket
import time

PORT = 1234
CLUSTER = [1, 2, 3]
LOGS = ['SET 5', 'ADD 1']

if __name__ == '__main__':
    time.sleep(10)  # Wait for leader election processs
    print('Client started')
    for node_id in CLUSTER:
        try:
            with ServerProxy(f'http://node_{node_id}:{PORT}') as node:
                if node.is_leader():
                    print(f"Node {node_id} is the cluster leader. Sending logs")
                    for log in LOGS:
                        if node.leader_receive_log(log):
                            print(f"Leader committed '{log}'")
                            time.sleep(5)  # Wait for entries to propagate
        except socket.error as e:
            print(f"Failed to connect to node_{node_id}: {e}")
