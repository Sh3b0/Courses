import argparse
import os
import time
import sys
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
        nodes[i] = node.Node(i)
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
    while True:
        print(f"\nPlease enter a query 'get_chord_info' or 'get_finger_table <port>'")
        inp = []
        try:
            inp = input().rstrip().split()
        except KeyboardInterrupt:
            print("\nExiting...")
            os._exit(0)

        if len(inp) == 1 and inp[0] == 'get_chord_info':
            print(REGISTRY.get_chord_info())
        elif len(inp) == 2 and inp[1].isdigit() and inp[0] == 'get_finger_table':
            try:
                print(nodes[int(inp[1])].get_finger_table())
            except KeyError:
                print(f"Port {inp[1]} is not assigned to any node.")
        else:
            print("Invalid query")
