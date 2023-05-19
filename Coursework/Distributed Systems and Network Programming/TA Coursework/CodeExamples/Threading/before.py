import time

db = []


def insert_into_db(name):
    time.sleep(1)
    db.append(name)
    print(f"{name} was inserted")


if __name__ == '__main__':
    t0 = time.time()

    for name in ["a", "b", "c", "d"]:
        insert_into_db(name)

    print(f"Time taken: {time.time() - t0}")
