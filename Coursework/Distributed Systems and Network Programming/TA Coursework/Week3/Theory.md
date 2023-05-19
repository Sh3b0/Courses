---
marp: true
title: Week 3 Theory
---

# Publisher-Subscriber (pub/sub) Pattern

- A messaging pattern in which senders (publishers) do not directly send data to receivers.
- Instead, messages are categorized into classes managed by a *broker* (middle-man)
  - Popular message broker software include [Apache Kafka](https://kafka.apache.org/), [RabbitMQ](https://rabbitmq.com/), and [ZeroMQ](https://zeromq.org/)
- Receivers (subscribers) may express interest in one or more classes and only receive messages from that class (called a topic in some systems).
- Avoid confusion with **producer-consumer** pattern (e.g., a manger is splitting tasks among workers)

---

# Pub/sub Advantages

- **Loose coupling**
  - In a client-server architecture, a client may not post a message successfully unless the server is alive and listening.
  - Pub/sub pattern removes this dependency, making it easier to program each side independently and connect different pieces together as needed.

---

# Pub/sub Advantages (cont.)

- **Scalability**
  - Pub/sub is especially useful for distributed systems in which independent components may have different processing capabilities.
  - A broker prevents a fast sender from overwhelming a slow receiver by queuing the messages (sometimes saving them to disk) for the subscribers to consume at their own pace.
  - More publishers (e.g., application users) can be handled by adding more subscribers (e.g., processing servers).

---

# Pub/sub disadvantages

- Introduces a single point of control that may be prone to failures and attacks
- Issues appear with message delivery as the system cannot guarantee that a subscriber got an expected message exactly once.

---

![bg fit](https://daxg39y63pxwu.cloudfront.net/images/blog/kafka-vs-rabbitmq/kafka_va_rabbitmq_performance.png)

---

# RabbitMQ Terminology

- **Message Queue:** buffer storing messages in the broker, can be saved to disk.
- **Connection:** a TCP connection between an application and the RabbitMQ broker.
- **Channel:** a virtual stream inside the TCP connection. Multiple channels may use the same connection.
- **Exchange:** receives messages from producers and pushes them to queues depending on rules defined by the exchange type. To receive messages, a queue needs to be bound to at least one exchange.

---

# RabbitMQ Terminology (cont.)

- **Binding:** a link between a queue and an exchange.
- **Routing key:** a key that the exchange looks at to decide how to route the message to queues.
- **AMQP:** Advanced Message Queuing Protocol is the application-layer protocol used by RabbitMQ for messaging (publishers/subscribers communicate with RMQ broker over port `5672/TCP`).
