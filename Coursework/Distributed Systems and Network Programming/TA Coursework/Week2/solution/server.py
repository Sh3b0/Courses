import random
import socket
import threading
from io import BytesIO

from PIL import Image

SERVER_IP = '0.0.0.0'
SERVER_PORT = 1234


def generate_random_image():
    image = Image.new('RGB', (10, 10))
    pixels = image.load()
    for i in range(10):
        for j in range(10):
            r = random.randint(0, 255)
            g = random.randint(0, 255)
            b = random.randint(0, 255)
            pixels[i, j] = (r, g, b)
    buffer = BytesIO()
    image.save(buffer, format='PNG')
    return buffer.getvalue()


def handle_client(sock, addr):
    image_bytes = generate_random_image()
    sock.sendall(image_bytes)
    sock.close()
    print(f'Sent an image to {addr}')


def run_server():
    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
        s.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
        s.bind((SERVER_IP, SERVER_PORT))
        try:
            s.listen()
            print(f'Listening on {SERVER_IP}:{SERVER_PORT}')
            while True:
                sock, addr = s.accept()
                threading.Thread(
                    target=handle_client, args=(sock, addr)).start()
        except KeyboardInterrupt:
            s.close()


if __name__ == '__main__':
    run_server()
