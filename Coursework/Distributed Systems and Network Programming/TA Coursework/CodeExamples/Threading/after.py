import time
from threading import Thread

db = []


def insert_into_db(name):
    time.sleep(1)
    db.append(name)
    print(f"{name} was inserted")


if __name__ == '__main__':
    t0 = time.time()
    threads = []

    for name in ["a", "b", "c", "d"]:
        threads.append(Thread(target=insert_into_db, args=(name,)))

    [t.start() for t in threads]
    [t.join() for t in threads]

    print(f"Time taken: {time.time() - t0}")
