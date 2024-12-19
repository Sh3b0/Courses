# Solutions for CSN Lecture Exercises

[TOC]

## Lecture 1

### Exercise 1

> Scenario: You are designing a distributed file system where files are stored across multiple servers. The goal is for users to be able to access and manipulate their files without worrying about where the files are stored, how many copies exist, or what happens if a server fails. Tasks:

#### 1- Identify Transparency Requirements

**a) List the types of transparency that are relevant for your distributed file system.**

In the described distributed file system, one mainly cares about

- **Access Transparency:** users accessing files the same way regardless of their location.
- **Replication Transparency:** users aren't aware/managing distributed copies of their files.
- **Failure Transparency:** if a node fails, the system should hide it as much as possible.

**b) Justify why each type is important for the users' experience.**

- **Access Transparency:** necessary for user experience (accessing files anywhere).
- **Replication Transparency:** necessary for reliability (files are never lost, and updates are saved).
- **Failure Transparency:** necessary for availability (service is always up and running).

#### 2- Design a Solution

**a) Propose a high-level design that addresses at least two types of transparency identified.**

- There can be a main database containing metadata (source of truth referencing file locations and replicas). Should also have a backup for that one.
- A distributed storage, organized by geographical location.

**b) Explain how your design hides the complexities of the distributed system from the users.**

- Metadata server maps logical file paths to physical locations, enabling access transparency.
- Automated replication and synchronization across nodes ensure replication transparency.

**c) Consider Failure Handling: Describe how your system can handle a situation where one of the file servers goes down while maintaining transparency.**

- Nodes should periodically do liveness checks on each other, ensuring peers are up, and reporting failures to metadata server.
- Requests to a down node are served by other replicas until it comes back up.

### Exercise 2

> The Domain Name System (DNS) is a critical component of the internet infrastructure that resolves human-readable domain names (e.g., example.com) into IP addresses. DNS supports several types of transparency, which help to hide the complexity of the system and make it more user- friendly. Which kind of transparencies are supported by the DNS?

- DNS is implemented as a distributed database of Resource Records (RRs), organized in a tree-like hierarchy with root servers on top and authoritative servers on leaves.
- This architecture enables the following types of transparency:
  - Access: users seem to access RRs the same way from any location
  - Failure: users rarely feel the failure of a DNS server. Due to replication and caching.
  - Replication: users are not aware and do not manage the synchronized copies of RRs.
  - Performance: caching on multiple levels ensure the speed of RR retrieval.

## Lecture 2

> Scenario: You are tasked with designing the architecture for a simple e-commerce website
> that sells books. The system should support the following functionalities:
>
> 1. **User Management**: Allow users to register, log in, and manage their profiles.
> 2. **Product Catalog**: Display a list of books, including details such as title, author, price,
>    and availability.
> 3. **Shopping Cart**: Enable users to add books to a shopping cart, view the cart, and
>    proceed to checkout.
> 4. **Order Management**: Process user orders and manage the order history.
> 5. **Payment Processing**: Integrate with a payment gateway to handle payments.
> 6. **Administration Panel**: Allow administrators to manage the product catalog, view orders,
>    and update inventory.

### 1- A list of core components

- **User Service:** Manages user registration, authentication, and profiles.
- **Product Catalog Service:** Handles book details, availability, and search functionality.
- **Shopping Cart Service:** Manages user-specific cart contents and actions.
- **Order Service:** Processes orders and maintains order history.
- **Payment Service:** Integrates with payment gateways for transactions.
- **Admin Service:** Provides tools for managing products, inventory, and viewing orders.
- **Database:** Stores user data, product catalog, cart contents, orders, and transaction records.

### 2- A description of how these components interact

- **User Service - Order Service:** Retrieve a user's order history.

- **Product Catalog Service - Shopping Cart Service:** Validate book availability before adding to the cart.

- **Shopping Cart Service - Order Service:** Transfer cart data to initiate order processing.

- **Order Service - Payment Service:** Trigger payment transactions and confirm payment success.

- **Admin Service - Product Catalog Service:** Update book inventory and prices.

### 3- A basic system architecture diagram

![](https://i.postimg.cc/BnkXxkhT/Untitled-Diagram-drawio.png)

### 4- A brief discussion on non-functional requirements.

- **Scalability:** Handle increasing user traffic and order volumes.
- **Performance:** Ensure low latency in product searches and checkout processes.
- **Security:** Protect sensitive user data and payment transactions.
- **Availability:** Guarantee uptime, especially during peak sales periods.
- **Maintainability:** Ensure modular design for easy updates and bug fixes.
- **Compliance:** Adhere to regulations like GDPR and PCI-DSS for data and payment security.

## Lecture 3

N/A

## Lecture 4

> **Scenario:** You are tasked with processing a stream of sensor data where each sensor reading is a string in the format: `sensor_id, timestamp, value`. Example: `001, 2024-09-01 12:00:00, 25.3`. The goal is to:
>
> - Filter sensor readings to only include those with values above 20.
> - Transform the readings into a JSON format.
> - Store the results in a file named `processed_readings.json`.

### 1- Pipes and Filters:
- Implement this pipeline using the Pipes and Filters pattern.
- Each stage of the pipeline (filter, transformation, storage) should be a separate module (function or class), connected by pipes (streams or queues).
- Ensure each filter is stateless and processes data as a stream.

```bash
#!/bin/bash

# Input: simulated sensor data stream
cat <<EOF > sensor_data_stream.txt
001, 2024-09-01 12:00:00, 25.3
002, 2024-09-01 12:01:00, 18.7
003, 2024-09-01 12:02:00, 21.0
EOF

# Filter stage: filter readings with values > 20
filter_stage() {
    awk -F, '$3+0 > 20 {print $0}' sensor_data_stream.txt
}

# Transformation stage: convert readings to JSON
transformation_stage() {
    jq -R -s -c '
    split("\n")[:-1] | 
    map(split(", ") | {sensor_id: .[0], timestamp: .[1], value: (.[2]|tonumber)})
    '
}

# Storage stage: save JSON to file
storage_stage() {
    local output_file=$1
    cat > "$output_file"
}

# Execute the pipeline
filter_stage | transformation_stage | storage_stage processed_readings.json

echo "Processed data saved to processed_readings.json"
```

![image-20241118190739955](https://i.postimg.cc/1tRJGTDP/image.png)

### 2- Producer-Consumer:

- Implement the same task using the Producer-Consumer model.
- Have one producer that reads sensor data and places it in a shared buffer.

- Multiple consumers will:
  - Consumer 1: Filter the data.
  - Consumer 2: Transform the filtered data.
  - Consumer 3: Write the transformed data to a file.
- Use a queue to handle communication between the producer and consumers.

```python
from threading import Thread
from time import sleep
import queue
import json
import random
from datetime import datetime

sensor_queue = queue.Queue() # Shared queue
output_file = "processed_readings.json"

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
```

![image-20241118200532463](https://i.postimg.cc/1XfBTqWj/image.png)

### 3- Describe your solution

> **Comparison:** After implementing both systems, answer the following questions:

a) How do Pipes and Filters differ from Producer-Consumer in terms of:

- Modularity (how easy it is to swap or modify a step in the pipeline)?
  - Pipe/filter seemed easier to modify without lots of changes
  - However, changing the producer code would likely require subsequent changes in consumer.
- Scalability (adding more filters or consumers)?
  - Pipes and Filters: High scalability, easy to add more filters or stages.
  - Producer-Consumer: Moderate scalability, adding consumers may cause contention over resources.
- Data handling (synchronous vs. asynchronous processing)?
  - Pipes and Filters: Asynchronous, stream-based processing: filters do not wait for their predecessors to process all data before .
  - Producer-Consumer: In my implementation, threads synchronously await the presence of elements in the queue to start working.

b) Which approach seems easier to manage as the complexity of the pipeline grows?

- Pipe/filter seemed easier to manage for its modularity and independence of steps.

c) In what scenarios would you choose one approach over the other?

- Pipes and Filters: Best for complex, multi-step data transformations where each step can be isolated and modified independently.
- Producer-Consumer: Best for scenarios when managing shared resources (like a queue) is necessary.

## Lecture 5

> Task: Create a producer-consumer system with two producers and one consumer. The producers will generate data, serialize it to JSON, and place it in a shared queue. The consumer will retrieve items from the queue, deserialize them from JSON, and process them.
>
> **Deliver:** a powerpoint (or pdf) including your commented code and two snapshots of runs,
> first with 2+1 processes, then 3+2

1- Create a shared queue (e.g., using Python's `queue.Queue` module) that can hold a limited
number of JSON-serialized items (e.g., dictionaries or objects).

2- Implement two producer functions,`producer1` and `producer2`, which generate random data, serialize it to JSON, and place it in the shared queue. Each producer should run in its own thread.

3- Implement a consumer function which retrieves items from the shared queue, deserializes
from JSON, and processes them. The consumer should run in its own thread as well.

4- Ensure proper synchronization between producers and the consumer (eg. using Python's threading mechanisms like `threading.Lock`).

5- Utilize a JSON library(e.g., the `json` module in Python) for serialization and deserialization.

6- Demonstrate the concurrent behavior of the system. Additional question: What happens with 3 producers and 2 consumers?

### Source Code

```python
from threading import Thread
import queue
import json
import random
import time

# Create a shared queue with a limited size
q = queue.Queue(maxsize=5)

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
```

### Execution Result

#### With 2 Producers and 1 Consumer

- Producers generate random data and put it into the queue.
  - If the queue is full, they block until the consumer processes some items.

- Consumer processes items as soon as they become available in the queue.
  - If the queue is empty, it blocks until new items are added by the producers.

![image-20241118221020004](https://i.postimg.cc/43s5GMLt/image.png)

#### With 3 Producers and 2 Consumer

- Three producers will fill up the queue faster. However, two consumers will also process data quicker.
- A desired (equilibrium) state happens when the production rate is close to the consumption rate.

![image-20241118221348167](https://i.postimg.cc/63Qf2ZNp/image.png)
