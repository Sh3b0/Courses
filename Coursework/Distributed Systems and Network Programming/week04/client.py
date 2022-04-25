import socket
import argparse
import time

BUF_SIZE = 2048
SERVER_IP = '127.0.0.1'
DEBUG_MODE = False


# Wait for data from sock or exit on interrupt
def safe_recv(sock):
    try:
        return sock.recv(BUF_SIZE)
    except KeyboardInterrupt:
        print("\nUser quit")
        exit()


# Connects to SERVER_IP:port using sock, for a certain purpose
def connect_to_server(sock, port, purpose):
    if DEBUG_MODE:
        print(f'Connecting to {SERVER_IP}:{port} from '
              f'127.0.0.1:{s.getsockname()[1]} to {purpose}')
    try:
        sock.connect((args.ip, port))
    except (OSError, ConnectionRefusedError):
        print("Server is unavailable")
    except KeyboardInterrupt:
        print('\nUser quit')
        exit()


if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('ip', type=str)
    parser.add_argument('port', type=int)
    args = parser.parse_args()

    # Usage example will be automatically generated for invalid usage
    server_port = int(args.port)

    # Connect to server to get game session
    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
        s.bind(('localhost', 0))
        connect_to_server(s, server_port, 'get game session')
        msg = safe_recv(s).decode()
        if msg.isdigit():
            game_port = int(msg)
        else:
            print(msg)
            exit()
        if DEBUG_MODE:
            print(f'Received game port {game_port}')

    # Wait for server to become ready (server timeouts after 5s)
    time.sleep(0.25)

    # Connect to server to play game
    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
        s.bind(('localhost', 0))
        connect_to_server(s, game_port, 'play game')
        while True:
            msg = safe_recv(s).decode()
            print(msg, end='')
            if msg == 'You win\n' or msg == 'You lose\n':
                if DEBUG_MODE:
                    print('Game finished')
                break
            try:
                s.send(input().encode())
            except KeyboardInterrupt:
                print("\nUser quit")
                exit()
