import os
import socket
import time
from io import BytesIO
from multiprocessing import Pool, cpu_count
from threading import Thread

from PIL import Image

SERVER_URL = '127.0.0.1:1234'
FILE_NAME = 'NameSurname.gif'
CLIENT_BUFFER = 1024
FRAME_COUNT = 5000
THREAD_COUNT = 10


def download_frames(offset, count):
    for i in range(offset, offset+count):
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


def download_frames_threaded():
    if not os.path.exists('frames'):
        os.mkdir('frames')
    batch_size = FRAME_COUNT // THREAD_COUNT
    threads = [Thread(target=download_frames,
                      args=(offset, batch_size,)) for offset in range(0, FRAME_COUNT, batch_size)]
    t0 = time.time()
    [t.start() for t in threads]
    [t.join() for t in threads]
    return time.time() - t0


def save_frames(frames):
    chunk = BytesIO()
    frames[0].save(chunk, format="GIF", append_images=frames[1:],
                   save_all=True, duration=500)
    return chunk


def create_gif_multiprocessed():
    t0 = time.time()

    frames = [Image.open(f"frames/{i}.png").convert("RGBA")
              for i in range(FRAME_COUNT)]
    batch_size = FRAME_COUNT // cpu_count()
    with Pool() as pool:
        chunks = pool.map(save_frames, [frames[offset:offset+batch_size]
                                        for offset in range(0, FRAME_COUNT, batch_size)])
    with open(FILE_NAME, "ab") as f:
        for chunk in chunks:
            f.write(chunk.getvalue())
    return time.time() - t0


if __name__ == '__main__':
    print(f"Frames download time: {download_frames_threaded()}")
    print(f"GIF creation time: {create_gif_multiprocessed()}")
