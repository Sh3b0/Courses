from threading import Thread
import queue
import json
import random
import time

# Create a shared queue with a limited size
q = queue.Queue(maxsize=2)

# Producer code (pr_id can be 1 or 2)
def producer(queue, pr_id):
    while True:
        data = {
            "id": pr_id,
            "value": random.randint(1, 100), # Random value
            "timestamp": time.time()
        }
        json_data = json.dumps(data)
        queue.put(json_data)
        print(f"Producer {pr_id} produced: {json_data}")
        time.sleep(random.uniform(0.1, 1))  # Random delay

# Consumer
def consumer(queue):
    while True:
        json_data = queue.get()  # Blocks until an item is available
        data = json.loads(json_data)
        print(f"Consumer processed: {data}")
        queue.task_done()
        time.sleep(random.uniform(0.1, 1))  # Simulate processing delay

# Start threads
p1_t = Thread(target=producer, args=(q, 1,), daemon=True)
p2_t = Thread(target=producer, args=(q, 2,), daemon=True)
c_t = Thread(target=consumer, args=(q,), daemon=True)

for thread in [p1_t, p2_t, c_t]:
    thread.start()

# Keep the main program running
try:
    while True:
        time.sleep(1)
except KeyboardInterrupt:
    print("\nTerminating...")