import socket
import argparse
import threading
import random

SERVER_IP = '127.0.0.1'
BUF_SIZE = 2048
DEBUG_MODE = False
PLAYERS_COUNT = 0


# Wait for data from con or exit on interrupt
def safe_recv(con):
    try:
        return con.recv(BUF_SIZE)
    except KeyboardInterrupt:
        print("\nServer is shutting down...")
        exit()


# Game logic
def game(port):
    global PLAYERS_COUNT
    if DEBUG_MODE:
        print(f'Game is running in thread {threading.current_thread().name}')

    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as gs:  # Game socket
        gs.bind((SERVER_IP, port))  # Random port
        gs.listen()
        gs.settimeout(5)
        try:
            con, _ = gs.accept()
        except socket.timeout:
            print("Client unavailable")
            return
        except KeyboardInterrupt:
            print("\nServer is shutting down...")
            exit()

        con.send(b'Welcome to the number guessing game!\nEnter the range: ')
        while True:
            data = safe_recv(con).decode().split(' ')

            if len(data) != 2 \
                    or not (data[0]).isdigit() \
                    or not (data[1]).isdigit() \
                    or int(data[0]) > int(data[1]):
                con.send(b'Invalid input.\nEnter the range: ')
            else:
                break

        answer = random.randint(int(data[0]), int(data[1]))
        con.send(b'You have 5 attempts\n')
        for a in range(4, -1, -1):
            while True:
                guess = safe_recv(con).decode()
                if not guess.isdigit():
                    con.send(b'Invalid input\nEnter a guess (integer): ')
                else:
                    break
            guess = int(guess)
            msg = ''
            if guess > answer:
                msg += 'Less\n'
            elif guess < answer:
                msg += 'Greater\n'
            else:
                con.send(b'You win\n')
                PLAYERS_COUNT -= 1
                return
            if a == 0:
                con.send(b'You lose\n')
                PLAYERS_COUNT -= 1
                return
            else:
                msg += f'You have {a} attempts\n'
                con.send(msg.encode())


if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('port', type=int)
    args = parser.parse_args()

    # Usage example will be automatically generated for invalid usage
    server_port = args.port

    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as ws:  # Welcoming socket
        try:
            ws.bind((SERVER_IP, server_port))
        except (PermissionError, OSError, OverflowError):
            print('Error while binding to the specified port')
            exit()

        print(f'Starting the server on {SERVER_IP}:{server_port}')
        ws.listen()
        while True:
            try:
                print('Waiting for a connection...')
                conn, addr = ws.accept()
            except KeyboardInterrupt:
                print('\nServer is shutting down...')
                exit()

            if PLAYERS_COUNT >= 2:
                print("The server is full")
                conn.send(b'The server is full')
                conn.close()
            else:
                print(f'Client connected')
                PLAYERS_COUNT += 1
                with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as test_sock:
                    test_sock.bind((SERVER_IP, 0))
                    game_port = test_sock.getsockname()[1]
                    if DEBUG_MODE:
                        print(f'Referring client to {game_port}')

                conn.send(str(game_port).encode())
                t = threading.Thread(name=f'p{PLAYERS_COUNT}', target=game, args=(game_port,))
                t.start()
                conn.close()
