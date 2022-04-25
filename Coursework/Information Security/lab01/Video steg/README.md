Script to hide/extract text data in video files.

```bash
pip install requirements.txt
```

```
usage: main.py [-h] [-m MESSAGE] [-e] [-v] video

positional arguments:
  video                 path to video file to operate on

optional arguments:
  -h, --help            show this help message and exit
  -m MESSAGE, --message MESSAGE
                        text to hide inside video. If not provided, default
                        sample will be used
  -e, --extract         extract hidden text from video
  -v, --verbose         show additional debug output
```

