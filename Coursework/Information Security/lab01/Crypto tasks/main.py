import argparse
import base64
from itertools import cycle


# 1. Substitution Cipher
def caesar_encode(plaintext, k):
    ciphertext = []
    for i in plaintext:
        if i.isupper():
            ciphertext.append(chr((ord(i) + k - ord('A')) % 26 + ord('A')))
        elif i.islower():
            ciphertext.append(chr((ord(i) + k - ord('a')) % 26 + ord('a')))
        else:
            ciphertext.append(i)
    return ''.join(ciphertext)


def caesar_decode(ciphertext, k):
    return caesar_encode(ciphertext, -k)


# 2 Transposition Cipher
def _build_fence(lst, n):
    fence = [[None] * len(lst) for n in range(n)]
    rail = [i for i in range(n - 1)] + [i for i in range(n - 1, 0, -1)]
    for i in range(len(lst)):
        fence[rail[i % len(rail)]][i] = lst[i]
    result = []
    for rail in fence:
        for c in rail:
            if c is not None:
                result.append(c)
    return result


def rail_fence_encode(plaintext, n):
    return ''.join(_build_fence(plaintext, n))


def rail_fence_decode(ciphertext, n):
    fence = _build_fence(list(range(len(ciphertext))), n)
    result = []
    for i in range(len(ciphertext)):
        result.append(ciphertext[fence.index(i)])
    return ''.join(result)


# 3. Simple XOR (base64 is used to have printable characters as encoded data)
def xor_encode(plaintext, key):
    ciphertext = []
    for i, j in zip(plaintext, cycle(key)):
        ciphertext.append(chr(ord(i) ^ ord(j)))
    return base64.b64encode(''.join(ciphertext).encode('ascii')).decode('ascii')


def xor_decode(ciphertext, key):
    plaintext = []
    ciphertext = base64.b64decode(ciphertext).decode('ascii')
    for i, j in zip(ciphertext, cycle(key)):
        plaintext.append(chr(ord(i) ^ ord(j)))
    return ''.join(plaintext)


def test(text):
    assert caesar_decode(caesar_encode(text, 3), 3) == text
    assert rail_fence_decode(rail_fence_encode(text, 3), 3) == text
    assert xor_decode(xor_encode(text, "key"), "key") == text


if __name__ == '__main__':
    # test('TestText#14')
    parser = argparse.ArgumentParser(formatter_class=argparse.RawTextHelpFormatter)
    parser.add_argument('-d', '--decode', action='store_true', help="text will be decoded")
    parser.add_argument('-a',
                        choices=['1', '2', '3'],
                        help="algorithm to use:\n\t1. Caesar cipher\n\t2. Rail Fence cipher\n\t3. Simple XOR",
                        required=True)
    parser.add_argument('-k', '--key', help="shifting factor, number of rails, or simple XOR key", required=True)
    parser.add_argument('text', help="text to encode (default) or decode (if -d is passed)")
    parser.usage = parser.format_help()[6:]  # to display usage on error
    args = parser.parse_args()

    if args.algorithm == '1' and args.key.isdigit():
        if args.decode:
            print(caesar_decode(args.text, int(args.key)))
        else:
            print(caesar_encode(args.text, int(args.key)))
    elif args.algorithm == '2' and args.key.isdigit():
        if args.decode:
            print(rail_fence_decode(args.text, int(args.key)))
        else:
            print(rail_fence_encode(args.text, int(args.key)))
    elif args.algorithm == '3':
        if args.decode:
            print(xor_decode(args.text, args.key))
        else:
            print(xor_encode(args.text, args.key))
    else:
        print("Invalid arguments.")

