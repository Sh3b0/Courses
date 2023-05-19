import binascii
import json
import os
from math import ceil
from socket import socket, AF_INET, SOCK_DGRAM

SERVER_PORT = 12346
BUF_SIZE = 128


def get_crc_checksum(file_contents):
    file_contents = (binascii.crc32(file_contents) & 0xFFFFFFFF)
    return "%08X" % file_contents


if __name__ == '__main__':
    s = socket(family=AF_INET, type=SOCK_DGRAM)
    s.bind(('localhost', SERVER_PORT))
    while True:
        print('Server waiting for new requests...')
        data = b''
        addr = ('', -1)
        file_info = {}

        try:
            data, addr = s.recvfrom(BUF_SIZE)
        except KeyboardInterrupt:
            print('Server is shutting down...')
            break

        print(f'User {addr} wants to send the file: {data.decode()}\n')

        try:
            file_info = json.loads(data.decode())
        except json.JSONDecodeError:
            s.sendto('Error while deserializing file info.'.encode(), addr)
            break

        # replying with server buffer size
        s.sendto(str(BUF_SIZE).encode(), addr)

        # download file from client
        if not os.path.exists('server_storage'):
            os.makedirs('server_storage')
        with open('server_storage' + os.path.sep + file_info['name'], 'wb') as file:
            tmp = []
            parts = ceil(file_info['size'] / BUF_SIZE)
            for i in range(parts):
                data, addr = s.recvfrom(BUF_SIZE)
                tmp.append(data)
                # print(f'Server received {round((i + 1.0) / parts * 100.0, 2)}% of the file')
            file.write(b''.join(tmp))

        # validate checksum
        with open('server_storage' + os.path.sep + file_info['name'], 'rb') as file:
            if get_crc_checksum(file.read()) != file_info['checksum']:
                s.sendto(str('ERROR').encode(), addr)
            else:
                s.sendto(str('OK').encode(), addr)
    s.close()
