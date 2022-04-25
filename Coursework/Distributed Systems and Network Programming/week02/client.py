from socket import socket, AF_INET, SOCK_DGRAM, timeout

SERVER_ADDR = 'localhost'
SERVER_PORT = 12346
CLIENT_PORT = 12345
CLIENT_BUF_SIZE = 2048


def esc():
    print("\nUser has quit.")
    s.close()
    exit()


if __name__ == '__main__':
    s = socket(family=AF_INET, type=SOCK_DGRAM)
    s.bind(('localhost', CLIENT_PORT))
    s.settimeout(5)
    while True:
        try:
            inp = input('Enter operator (+, -, *, /, >, <, >=, <=) then two operands: ')
            if inp.lower() == 'exit':
                esc()
            s.sendto(inp.encode(), (SERVER_ADDR, SERVER_PORT))
        except KeyboardInterrupt:
            esc()

        # print('Awaiting server response...')
        try:
            data, addr = s.recvfrom(CLIENT_BUF_SIZE)
            print(f'Result: {data.decode()}\n')
        except KeyboardInterrupt:
            esc()
        except timeout:
            print('Server did not respond in time.')
