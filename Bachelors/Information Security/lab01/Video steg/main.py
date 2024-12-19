import os
import argparse
import shutil
import tqdm
import cv2
from math import ceil
from subprocess import call, STDOUT
from stegano import lsb
from shutil import which

TEXT = 'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore ' \
       'magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea ' \
       'commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat ' \
       'nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit ' \
       'anim id est laborum.'


def split_into_frames(video):
    c = 0
    while True:
        success, image = video.read()
        if not success:
            break
        cv2.imwrite(os.path.join(f'tmp', f'{c}.png'), image)
        c += 1
    return c


def video_steg_hide(video_path, message):
    if not os.path.exists(video_path):
        print(f"No such file: {video_path}")
        return

    if not os.path.exists('tmp'):
        os.mkdir('tmp')

    video = cv2.VideoCapture(video_path)

    # Extract audio from video (ignoring output, printing errors)
    call(["ffmpeg", "-i", args.video, "-q:a", "0", "-map", "a", "tmp/audio.mp3", "-y"],
         stdout=open(os.devnull, "w"),
         stderr=STDOUT)

    frame_count = split_into_frames(video)

    # Hide message in frames
    n = max(1, ceil(len(message) / frame_count))
    blocks = [message[i: i + n] for i in range(0, len(message), n)]
    for i in tqdm.tqdm(range(min(frame_count, len(blocks)))):
        secret = lsb.hide(f'tmp/{i}.png', blocks[i])
        secret.save(f'tmp/{i}.png')
        if args.verbose:
            print(f'{blocks[i]} is now hidden in frame {i}')

    # Recreate video from modified frames and re-add audio.
    call(["ffmpeg", "-i", f"tmp/%d.png", "-vcodec", "png", f"tmp/video.mov", "-y"],
         stdout=open(os.devnull, "w"),
         stderr=STDOUT)
    call(["ffmpeg", "-i", "tmp/video.mov", "-i", "tmp/audio.mp3", "-map", "0", "-map", "1:a",
          "-c:v", "copy", "-shortest", "output.mp4"], stdout=open(os.devnull, "w"), stderr=STDOUT)
    print("Secret buried successfully")


def video_steg_extract(video_path):
    if not os.path.exists(video_path):
        print(f"No such file: {video_path}")
        return

    if not os.path.exists('tmp'):
        os.mkdir('tmp')

    video = cv2.VideoCapture(video_path)
    frame_count = split_into_frames(video)
    message = ''

    itr = tqdm.tqdm(range(frame_count), leave=True)
    for i in itr:
        secret = lsb.reveal(f'tmp/{i}.png')
        if not secret:
            itr.close()
            break
        if args.verbose:
            print(f'{secret} was extracted from frame {i}')
        message += secret
        
    if message == '':
        return f"No hidden message found in {video_path}"
    return message


if __name__ == '__main__':
    if which('ffmpeg') is None:
        print('ffmpeg is required for this script to work, install it and try again')
        exit()

    parser = argparse.ArgumentParser()
    parser.add_argument('-m', '--message',
                        help='text to hide inside video. If not provided, default sample will be used')
    parser.add_argument('-e', '--extract', help='extract hidden text from video', action='store_true')
    parser.add_argument('video', help='path to video file to operate on')
    parser.add_argument('-v', '--verbose', action='store_true', help='show additional debug output')
    args = parser.parse_args()

    if args.extract:
        print("Extracted message: \n" + video_steg_extract(args.video))
    elif args.message:
        video_steg_hide(args.video, args.message)
    else:
        video_steg_hide(args.video, TEXT)

    if not args.verbose:
        shutil.rmtree('tmp')
