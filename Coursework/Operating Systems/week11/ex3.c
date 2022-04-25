Program 1 prints "Hello" without flushing stdout
After the fork, both parent and child flush stdout.
Therefore, two "Hello"s are printed

Program 2 prints "Hello\n", thus flushing stdout
After the fork, only the child flushes the stdout, printing an empty line.
