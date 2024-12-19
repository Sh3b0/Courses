from threading import Thread
import queue
import json
from time import sleep
import random
from datetime import datetime

# Shared queue
sensor_queue = queue.Queue()

# File to store processed readings
output_file = "processed_readings.json"

# Producer function
def producer():
    sample_data = ["001, 2024-09-01 12:00:00, 25.3",
                   "002, 2024-09-01 12:01:00, 18.7",
                   "003, 2024-09-01 12:02:00, 21.0"]
    for item in sample_data:
        sensor_queue.put(item)
        print(f"Produced: {item}")
        sleep(1)  # Simulate delay between readings

# Consumer 1: Filter readings with values > 20
def c1(input_queue, output_queue):
    while True:
        reading = input_queue.get()
        sensor_id, timestamp, value = reading.split(", ")
        if float(value) > 20.0:
            output_queue.put((sensor_id, timestamp, float(value)))
            print(f"C1: filtered sensor {sensor_id} reading")
        input_queue.task_done()

# Consumer 2: Transform readings to JSON format
def c2(input_queue, output_queue):
    while True:
        sensor_id, timestamp, value = input_queue.get()
        reading_json = {
            "sensor_id": sensor_id,
            "timestamp": timestamp,
            "value": value,
        }
        output_queue.put(reading_json)
        print(f"C2: transformed sensor {sensor_id} reading")
        input_queue.task_done()

# Consumer 3: Store readings in a JSON file
def c3(input_queue):
    while True:
        reading = input_queue.get()
        with open(output_file, "a") as f:
            f.write(f"{json.dumps(reading)}\n")
            print(f"C3: stored sensor {reading["sensor_id"]} reading")
        input_queue.task_done()

# Create additional queues for intermediate steps
filtered_queue = queue.Queue()
transformed_queue = queue.Queue()

# Create threads
t0 = Thread(target=producer, daemon=True)
t1 = Thread(target=c1, args=(sensor_queue, filtered_queue), daemon=True)
t2 = Thread(target=c2, args=(filtered_queue, transformed_queue), daemon=True)
t3 = Thread(target=c3, args=(transformed_queue,), daemon=True)

# Start threads
[t.start() for t in [t0, t1, t2, t3]]

# Keep the main thread running
try:
    while True:
        sleep(1)
except KeyboardInterrupt:
    print("\nTerminating...")