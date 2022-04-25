from socket import socket, AF_INET, SOCK_DGRAM

SERVER_PORT = 12346
SERVER_BUF_SIZE = 2048

s = socket(family=AF_INET, type=SOCK_DGRAM)
s.bind(('localhost', SERVER_PORT))

while True:
    print('Server waiting for new requests...')
    data = b''
    addr = ('', -1)

    try:
        data, addr = s.recvfrom(SERVER_BUF_SIZE)
    except KeyboardInterrupt:
        print("Server is shutting down...")
        s.close()
        exit()

    print(f'Request by {addr}: {data.decode()}\n')
    try:
        [opr, op1, op2] = data.decode().split(' ')
        if '.' in op1:
            op1 = float(op1)
        else:
            op1 = int(op1)

        if '.' in op2:
            op2 = float(op2)
        else:
            op2 = int(op2)

    except ValueError:
        s.sendto('parsing error.'.encode(), addr)
        continue

    res = 0
    if opr == '+':
        res = op1 + op2
    elif opr == '-':
        res = op1 - op2
    elif opr == '*':
        res = op1 * op2
    elif opr == '/':
        if op2 != 0:
            res = op1 / op2
        else:
            s.sendto('division by 0.'.encode(), addr)
            continue
    elif opr == '>':
        s.sendto(str(op1 > op2).encode(), addr)
    elif opr == '<':
        s.sendto(str(op1 < op2).encode(), addr)
    elif opr == '>=':
        s.sendto(str(op1 >= op2).encode(), addr)
    elif opr == '<=':
        s.sendto(str(op1 <= op2).encode(), addr)
    else:
        s.sendto('operator not supported.'.encode(), addr)
        continue

    # print(f'Result: {res}\n')
    s.sendto(str(res).encode(), addr)
