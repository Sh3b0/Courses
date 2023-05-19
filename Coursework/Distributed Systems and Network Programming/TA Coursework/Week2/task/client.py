import os
import socket
import time

from PIL import Image

SERVER_URL = '127.0.0.1:1234'
FILE_NAME = 'NameSurname.gif'
CLIENT_BUFFER = 1024
FRAME_COUNT = 5000


def download_frames():
    t0 = time.time()
    if not os.path.exists('frames'):
        os.mkdir('frames')
    for i in range(FRAME_COUNT):
        with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
            ip, port = SERVER_URL.split(':')
            s.connect((ip, int(port)))
            image = b''
            while True:
                packet = s.recv(CLIENT_BUFFER)
                if not packet:
                    break
                image += packet
            with open(f'frames/{i}.png', 'wb') as f:
                f.write(image)
    return time.time() - t0


def create_gif():
    t0 = time.time()
    frames = []
    for frame_id in range(FRAME_COUNT):
        frames.append(Image.open(f"frames/{frame_id}.png").convert("RGBA"))
    frames[0].save(FILE_NAME, format="GIF",
                   append_images=frames[1:], save_all=True, duration=500, loop=0)
    return time.time() - t0


if __name__ == '__main__':
    print(f"Frames download time: {download_frames()}")
    print(f"GIF creation time: {create_gif()}")
