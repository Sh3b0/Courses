import argparse
import os
import time
import sys
import zlib

import registry
import node

nodes = {}
REGISTRY_IP = 'localhost'
REGISTRY_PORT = 50000
VERBOSE = False

if __name__ == '__main__':
    # Parse arguments
    parser = argparse.ArgumentParser()
    parser.add_argument('-m', type=int, default=5, help='length of identifiers in chord (in bits)')
    parser.add_argument('-v', '--verbose', action='count', default=0, help='shows debugging info')
    parser.add_argument('first_port', type=int, help='first port number that nodes can use')
    parser.add_argument('last_port', type=int, help='last port number that nodes can use')
    args = parser.parse_args()

    if args.verbose:
        VERBOSE = True
        registry.VERBOSE = True
        node.VERBOSE = True

    if REGISTRY_PORT in range(args.first_port, args.last_port + 1):
        print(f"Port range for nodes conflicts with registry port {REGISTRY_PORT}. Aborting...")
        exit()

    # Create registry server in a separate thread
    REGISTRY = registry.Registry(args.m)
    REGISTRY.start()

    # Create nodes (each in a separate thread)
    rng = range(args.first_port, args.last_port + 1)
    for i in rng:
        nodes[i] = node.Node(i, args.m)
    for i in rng:
        nodes[i].start()

    print("Creating nodes and constructing finger tables. Please wait...")
    while node.progress < len(rng):
        time.sleep(0.5)
        if not VERBOSE:  # Nice progress bar for non-verbose runs
            percent = node.progress / len(rng) * 100
            sys.stdout.write('\r')
            sys.stdout.write("Completed: [{:{}}] {:>3}%"
                             .format('=' * int(percent / (100.0 / 30)),
                                     30, int(percent)))
            sys.stdout.flush()

    print(f"\nRegistry and {len(registry.nodes)} nodes created.")

    # Taking user input for testing
    print(f"\nValid queries: \n=====================\n"
          f"get_chord_info\n"
          f"get_finger_table <port>\n"
          f"get <port> <filename>\n"
          f"save <port> <filename>\n"
          f"quit <port>")
    while True:
        print(f"\nPlease enter a query: ")
        inp = []
        try:
            inp = input().rstrip().split()
        except KeyboardInterrupt:
            print("\nExiting...")
            os._exit(0)
        try:
            if len(inp) == 1 and inp[0] == 'get_chord_info':
                print(REGISTRY.get_chord_info())
            elif len(inp) == 2 and inp[0] == 'get_finger_table' and inp[1].isdigit():
                print(nodes[int(inp[1])].get_finger_table())
            elif len(inp) == 2 and inp[0] == 'quit':
                print(nodes[int(inp[1])].quit())
                del nodes[int(inp[1])]
            elif len(inp) == 3 and (inp[0] == 'save' or inp[0] == 'get') and inp[1].isdigit():
                hash_value = zlib.adler32(inp[2].encode())
                target_id = hash_value % 2 ** args.m
                print(f'{inp[2]} has identifier {target_id}')
                print(nodes[int(inp[1])].file(target_id, inp[2], inp[0]))
            else:
                print("Invalid query")
        except KeyError:
            print(f"Node with port {inp[1]} isn\'t available")
