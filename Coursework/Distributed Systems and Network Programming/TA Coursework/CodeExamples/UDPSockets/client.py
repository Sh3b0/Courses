import socket

HOST = "127.0.0.1"
PORT = 8080

with socket.socket(socket.AF_INET, socket.SOCK_DGRAM) as s:
    while True:
        s.sendto(input("Message to send: ").encode(), (HOST, PORT))
