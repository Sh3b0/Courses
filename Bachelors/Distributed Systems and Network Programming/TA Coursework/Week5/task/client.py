from xmlrpc.client import ServerProxy
import random

M = 5
PORT = 1234
RING = [2, 7, 11, 17, 22, 27]


def lookup(node_id: int, key: int) -> None:
    """Calls the get method for a remote node over RPC and print the result"""
    with ServerProxy(f'http://node_{node_id}:{PORT}') as node:
        print(f"lookup({node_id}, {key}) = {node.get(key)}")


if __name__ == '__main__':
    print("Client started")

    # Asking a random node to insert an entry into the DHT
    # String keys should be consistently hashed to an integer value between 0 and (2**M)-1
    for i in range(2 ** M):
        with ServerProxy(f'http://node_{random.choice(RING)}:{PORT}') as node:
            node.put(i, f"value_{i}")

    # Querying a DHT node for the value of a certain key.
    lookup(2, 20)
    lookup(11, 15)
    lookup(17, 1)
    lookup(22, 27)
    lookup(2, 5)
