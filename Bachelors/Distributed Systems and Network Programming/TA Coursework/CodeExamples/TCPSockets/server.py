import socket

HOST = "127.0.0.1"
PORT = 8080

with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
    s.bind((HOST, PORT))
    s.listen()
    conn, addr = s.accept()
    with conn:
        print(f"Accepted a connection from {addr}")
        while True:
            data = conn.recv(1024)
            if not data:
                break
            print(f"Client: {data.decode()}")
