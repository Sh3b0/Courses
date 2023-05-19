import os
from socket import socket, AF_INET, SOCK_DGRAM, timeout
import time
import random

SERVER_PORT = 12346
BUF_SIZE = 20480  # bytes

# For how long server stores history of completed transfers
FILE_LIFETIME = 20  # seconds

# Time before server forgets sessions of inactive clients
INACTIVITY_LIMIT = 5  # seconds

# Stores client sessions (addr, Transfer)
sess = {}

# Changing to True will drop some ACKs randomly
TESTING_MODE = False


# Represents a Connection for file transfer
class Transfer:
    def __init__(self, ext, size):
        self.expected_sqno = 1
        self.file_ext = ext
        self.file_size = size
        self.file_content = []
        self.last_rcpt_ts = time.time()
        self.complete = False


# Handle packets containing connection initiation
def handle_start():
    global sess
    packet = data.decode().split('|')
    # print(f'Client {addr} sent a start packet.')
    if sess.get(addr) is not None:
        print("A session already exists with this client. Aborting...")
        return

    sess[addr] = Transfer(packet[2], packet[3])

    if not os.path.exists('server_storage'):
        os.makedirs('server_storage')

    ack = f'a|{int(packet[1]) + 1}|{BUF_SIZE}'
    s.sendto(ack.encode(), addr)


# Handle packets containing data
def handle_data():
    global sess
    packet = data[:4].decode().split('|')
    # print(f'Client {addr} sent a data packet.')

    if sess.get(addr) is None:
        # Client sending data without start packet (session was forgotten)
        print('Who are you?')
        return

    transfer = sess[addr]
    if int(packet[1]) != transfer.expected_sqno:
        print('Received duplicated packet.')  # won\'t append content, but will resend ACK.
        ack = f'a|{(int(packet[1]) + 1) % 2}'
        s.sendto(ack.encode(), addr)
    else:
        # print('Received a new file packet.')  # send ACK and append contents.
        ack = f'a|{(int(packet[1]) + 1) % 2}'
        transfer.expected_sqno = (int(packet[1]) + 1) % 2

        # For testing: drops ACKs randomly
        if TESTING_MODE:
            if random.randint(0, 3) != 0:
                s.sendto(ack.encode(), addr)
        else:
            s.sendto(ack.encode(), addr)

        with open('server_storage' + os.path.sep + f'{hash(transfer)}.{transfer.file_ext}', 'wb') as f:
            transfer.file_content.append(data[4:])
            so_far = len(b''.join(transfer.file_content))
            print(f'Server received {round(so_far / int(transfer.file_size) * 100.0, 2)}% of the file')
            transfer.last_rcpt_ts = time.time()
            if so_far == int(transfer.file_size):
                print('File download complete.')
                f.write(b''.join(transfer.file_content))
                transfer.complete = True


# Checks and removes sessions for inactive clients or old history
def memory_checks():
    global sess
    # print(f'All sessions: {len(sess)}')
    for item in list(sess.items()):
        elapsed_time = time.time() - item[1].last_rcpt_ts
        file_loc = 'server_storage' + os.path.sep + f'{hash(item[1])}.{item[1].file_ext}'
        if item[1].complete:
            if elapsed_time > FILE_LIFETIME:
                print(f'Deleted history of completed session with {item[0]}')
                if os.path.exists(file_loc):
                    os.remove(file_loc)
                del sess[item[0]]

        elif time.time() - item[1].last_rcpt_ts > INACTIVITY_LIMIT:
            print(f'Session expired for inactive {item[0]}')
            if os.path.exists(file_loc):
                os.remove(file_loc)
            del sess[item[0]]


if __name__ == '__main__':
    with socket(family=AF_INET, type=SOCK_DGRAM) as s:
        s.settimeout(1)
        s.bind(('localhost', SERVER_PORT))
        print('Server started...')
        while True:
            # print('\nServer is waiting for new requests...')
            data = b''
            addr = ('', -1)

            try:
                data, addr = s.recvfrom(BUF_SIZE)
            except KeyboardInterrupt:
                print('Server is shutting down...')
                break
            except timeout:
                memory_checks()
                continue

            packet_type = data[:1].decode()

            if packet_type == 's':
                print(f'{addr}: {data.decode()}')
                handle_start()

            elif packet_type == 'd':
                print(f'{addr}: {data[:4].decode()}some_data')
                handle_data()
