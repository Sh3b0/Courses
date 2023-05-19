# Week 3 - Message Brokers

> Distributed Systems and Network Programming - Spring 2023

## Overview

Your task for this lab is to use [RabbitMQ](https://rabbitmq.com/) as a message broker for a pollution monitoring system.

- A sensor **(publisher)** collects CO~2~ levels in the environment and sends the obtained values to a server.
- The server **(subscriber)** processes these values and indicates if CO~2~ levels are abnormal.

## System Architecture

### **Architecture diagram**

![diagram](https://user-images.githubusercontent.com/40727318/229937239-2c27b9f4-e484-4eb9-b174-768ed8102b75.svg)

### **Directory structure**

```bash
.
├── docker-compose.yml
├── publishers
│   ├── sensor.py
│   └── control-tower.py
└── subscribers
    ├── receiver.py
    └── reporter.py
```

### **Publishers**

- **Sensor (`sensor.py`)**
  - Reads user input (an integer) representing the current level of CO~2~.
  - Sends the value along with the current time to the server in JSON format.
    - Values should be sent to the CO2 queue for processing only by the receiver
  - Example message: `{"time": "2023-04-03 17:19:29", "value": 500}`

- **Control tower (`control-tower.py`)**
  - Takes user input (a string) representing a query to the reporter.
  - The query can either be `current` or `average`
    - `current`: asks for the current CO~2~ level.
    - `average`: asks for the average of all collected values since the system started.

### **Subscribers**

- **Receiver (`receiver.py`)**
  - Listens for messages from the sensor.
  - Extracts the received JSON data and appends it to a file (e.g., `receiver.log`).
  - Checks the received (latest) CO~2~ value
    - If the value is larger than `500`, the server prints a `WARNING` message.
    - Otherwise, print `OK`
- **Reporter (`reporter.py`)**
  - Listens for messages (queries) from the control tower.
  - Prints the answer upon receiving a query for `current` or `average`.
  - The answer can be calculated based on the data from `receiver.log`

### Message Broker (RabbitMQ Server)

- The broker is maintaining one or more message queues.
- Producers push messages to queues for consumers to receive.
- An exchange controls which messages should be routed to which queues and how it's done.
  - Multiple exchange types exist, use the `topic` exchange type which allows routing messages to different queues based on a wildcard (`*`) routing pattern.
- A binding connects the exchange to a certain queue, the diagram above shows which queues should be bound to the exchange and the routing patterns to use.

## Task

1. Run RabbitMQ in a docker container using [docker-compose](https://docs.docker.com/compose/install/) by executing the following command in the same directory as `docker-compose.yml` file.

   ```bash
   docker-compose up
   ```

2. The Web UI should be available at <http://localhost:15672/>
   - You can see default login credentials (`rabbit:1234`) in the compose file.
3. Install [`pika`](https://pypi.org/project/pika/), the Python library for interacting with RabbitMQ.
   - It's recommended to create a virtual environment instead of installing `pika` globally.
   - You can do so by running the following commands (you may need to install `python3-venv`)

     ```bash
     python -m venv venv
     source venv/bin/activate
     pip install pika
     ```

4. Implement the system components as described above. Refer to [this tutorial](https://www.rabbitmq.com/tutorials/tutorial-three-python.html) and [pika documentation](https://pika.readthedocs.io/en/stable/index.html) for help.
   - Connect to RabbitMQ server at `localhost` and create a channel.
   - Use `amq.topic` exchange, or create your own exchange of type `topic`
   - Send sensor values with a routing key starting with `co2` (e.g., `co2.sensor`)
   - Send control queries with a routing key starting with `rep` (e.g., `rep.current` and `rep.average`)
   - Configure the receiver to listen for `co2.*` and the reporter to listen for `rep.*`
   - Don't forget to `ack` the received messages so that they do not remain in the queue.
5. Submit a single ZIP archive named `NameSurname.zip` with `publishers` and `subscribers` directories inside.

## Example Run

```bash
$ python publishers/sensor.py
Enter CO2 level: 499
Enter CO2 level: 500
Enter CO2 level: 501
Enter CO2 level: 500
```

```bash
$ python subscribers/receiver.py
[*] Waiting for CO2 data. Press CTRL+C to exit
2023-04-03 17:19:29: OK
2023-04-03 17:20:02: OK
2023-04-03 17:21:05: WARNING
2023-04-03 17:22:08: OK
```

---

```bash
$ python publishers/control-tower.py
Enter Query: current
Enter Query: average
```

```bash
$ python subscribers/reporter.py
[*] Waiting for queries from the control tower. Press CTRL+C to exit
2023-04-03 17:19:30: Latest CO2 level is 499
2023-04-03 17:23:02: Average CO2 level is 500.0
```

## Checklist

- [ ] A single submitted file `NameSurname.zip` with the following content inside:

  ```bash
  .
  ├── publishers
  │   ├── sensor.py
  │   └── control-tower.py
  └── subscribers
      ├── receiver.py
      └── reporter.py
  ```

- [ ] Publisher scripts correctly read and process user input.
- [ ] Subscriber scripts only receive intended messages.
- [ ] Receiver correctly processes sensor values and prints timestamped `WARNING` or `OK` messages accordingly.
- [ ] Reporter correctly processes control tower queries and prints the required answers.
- [ ] The code does not use any external dependencies (apart from  [`pika`](https://pypi.org/project/pika/))
- [ ] The source code is the author's original work. Both parties will be penalized for detected plagiarism.
