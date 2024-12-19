from socket import socket, AF_INET, SOCK_DGRAM, timeout
import os
import math
import time
import random

FILE_PATH = 'client_storage/img.jpg'
CLIENT_PORT = 12345
CLIENT_BUF_SIZE = 2048
SERVER_PORT = 12346
SERVER_ADDR = 'localhost'
SERVER_BUF_SIZE = -1  # We don't know yet

# Changing to True will drop some data packets randomly
TESTING_MODE = False


def get_start():
    if not os.path.exists(FILE_PATH):
        print('File does not exist')
        exit()

    with open(FILE_PATH, 'rb') as file:
        _, ext = os.path.splitext(file.name)
        size = os.path.getsize(FILE_PATH)

    return f's|0|{ext[1:]}|{size}'.encode()


def wait_ack(packet):
    global SERVER_BUF_SIZE
    s.settimeout(0.5)
    tr = 1
    while tr < 6:
        try:
            # For testing: drops data packets randomly
            if TESTING_MODE:
                if random.randint(0, 3) != 0:
                    s.sendto(packet, (SERVER_ADDR, SERVER_PORT))
            else:
                s.sendto(packet, (SERVER_ADDR, SERVER_PORT))

            data, addr = s.recvfrom(CLIENT_BUF_SIZE)
            print(f'Received ACK: {data.decode()}')
            ack = data.decode().split('|')

            if len(ack) == 3:
                SERVER_BUF_SIZE = int(ack[2])
            break

        except KeyboardInterrupt:
            print('User has quit')
            exit()
        except timeout:
            print("Timeout. Retrying...")
            tr += 1

    if tr == 6:
        print('Receiver isn\'t available or refuses connection.')
        exit()


def upload_file():
    global SERVER_BUF_SIZE
    if not os.path.exists(FILE_PATH):
        print('File does not exist')
        exit()

    file_total_size = os.path.getsize(FILE_PATH)
    packet_size_wo_data = len('d|0|'.encode())
    file_part_size = SERVER_BUF_SIZE - packet_size_wo_data
    parts = math.ceil(file_total_size / file_part_size)
    with open(FILE_PATH, 'rb') as f:
        for i in range(parts):
            print(f'Sending file part {i + 1}')
            r = f.read(file_part_size)

            # Intentional delay to test inactivity limit.
            # Server will abandon transfer if delay exceeded 5 seconds
            # time.sleep(6)

            if i % 2:
                packet = b'd|0|' + r
            else:
                packet = b'd|1|' + r
            wait_ack(packet)


if __name__ == '__main__':
    with socket(family=AF_INET, type=SOCK_DGRAM) as s:
        s.bind(('localhost', CLIENT_PORT))
        start = get_start()
        print('Sending start packet...')
        wait_ack(start)
        upload_file()
