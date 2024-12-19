import sched
import time
from threading import Thread


def task(param):
    global t0, event
    print(f"{time.time() - t0} seconds elapsed since the last call. Arg {param}")
    t0 = time.time()
    event = s.enter(delay=1, priority=1, action=task, argument=[1])


if __name__ == '__main__':
    s = sched.scheduler()
    t0 = time.time()
    event = s.enter(delay=1, priority=1, action=task, argument=[1])
    Thread(target=s.run).start()

    print("Inspecting the scheduler from the main thread")

    while True:
        try:
            print(f"Upcoming events: {s.queue}")
            time.sleep(5)
        except KeyboardInterrupt:
            if not s.empty():
                s.cancel(event)
            else:
                break
