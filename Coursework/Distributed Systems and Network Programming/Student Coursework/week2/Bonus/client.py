from socket import socket, AF_INET, SOCK_DGRAM, timeout
import binascii
import json
import os
from math import ceil

FILE_PATH = 'client_storage/innopolis.jpg'
CLIENT_PORT = 12345
SERVER_PORT = 12346
SERVER_ADDR = 'localhost'
CLIENT_BUF_SIZE = 2048
SERVER_BUF_SIZE = -1  # We don't know yet


def get_crc_checksum(file_contents):
    file_contents = (binascii.crc32(file_contents) & 0xFFFFFFFF)
    return "%08X" % file_contents


def send_file_metadata():
    file_info = {}
    if not os.path.exists(FILE_PATH):
        print('File does not exist')
        exit()
    with open(FILE_PATH, 'rb') as file:
        file_info['name'] = os.path.basename(file.name)
        file_info['size'] = os.path.getsize(FILE_PATH)
        file_info['checksum'] = get_crc_checksum(file.read())

        dmp = json.dumps(file_info)
        s.sendto(dmp.encode(), (SERVER_ADDR, SERVER_PORT))

    print('File metadata sent successfully')
    return file_info


def esc(msg):
    print(msg)
    s.close()
    exit()


if __name__ == '__main__':
    s = socket(family=AF_INET, type=SOCK_DGRAM)
    s.bind(('localhost', CLIENT_PORT))
    file_metadata = send_file_metadata()

    # print('Awaiting server response...')
    s.settimeout(1)
    try:
        data, addr = s.recvfrom(CLIENT_BUF_SIZE)
        print(f'Server buffer size: {data.decode()}')
        SERVER_BUF_SIZE = int(data.decode())
    except KeyboardInterrupt:
        esc('User has quit')
    except timeout:
        esc("The server isn't available.")

    # upload file
    while True:
        with open(FILE_PATH, 'rb') as f:
            for i in range(ceil(file_metadata['size'] / SERVER_BUF_SIZE)):
                data = f.read(SERVER_BUF_SIZE)
                s.sendto(data, (SERVER_ADDR, SERVER_PORT))

        # print('Awaiting server response...')
        try:
            data, addr = s.recvfrom(CLIENT_BUF_SIZE)
            if data.decode() != 'OK':
                print('Upload error, retrying...')
            else:
                esc('File uploaded successfully.')
        except KeyboardInterrupt:
            esc('User has quit')
        except timeout:
            esc("The server isn't available.")
