import time


def isprime(n):
    print(f"MainProcess started calculating the primeness of {n}")
    if n in (2, 3):
        return True
    if n % 2 == 0:
        return False
    for divisor in range(3, n):
        if n % divisor == 0:
            return False
    return True


if __name__ == '__main__':
    numbers = [15492781, 15492787, 15492803, 15492811,
               15492810, 15492833, 15492859, 15502547]

    t0 = time.time()

    for num in numbers:
        isprime(num)

    print(f"Total time taken: {time.time() - t0}")
