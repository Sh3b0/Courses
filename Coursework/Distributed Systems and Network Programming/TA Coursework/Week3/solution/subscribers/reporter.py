import json
import os
from datetime import datetime

from pika import BlockingConnection, ConnectionParameters, PlainCredentials
from pika.exchange_type import ExchangeType

RMQ_HOST = 'localhost'
RMQ_USER = 'rabbit'
RMQ_PASS = '1234'
EXCHANGE_NAME = 'amq.topic'
ROUTING_KEY = 'rep.*'


def callback(channel, method, properties, body):
    query = json.loads(body.decode('utf-8'))['query']
    if not os.path.exists('receiver.log'):
        print("Receiver logs were not found")
        return
    with open('receiver.log', 'r') as f:
        data = [json.loads(line) for line in f.readlines()]
        now = datetime.now().strftime('%Y-%m-%d %H:%M:%S')
        if query == 'current':
            print(
                f"{now}: Latest CO2 level is {data[-1]['value']}")
        elif query == 'average':
            avg = 0.0
            for item in data:
                avg += float(item['value'])
            avg /= len(data)
            print(f"{now}: Average CO2 level is {avg}")


if __name__ == '__main__':
    connection = BlockingConnection(
        ConnectionParameters(
            host=RMQ_HOST,
            credentials=PlainCredentials(RMQ_USER, RMQ_PASS)
        )
    )
    channel = connection.channel()
    channel.exchange_declare(
        exchange=EXCHANGE_NAME,
        exchange_type=ExchangeType.topic,
        durable=True
    )
    queue_name = channel.queue_declare(queue='', exclusive=True).method.queue
    channel.queue_bind(
        exchange=EXCHANGE_NAME,
        queue=queue_name,
        routing_key=ROUTING_KEY
    )

    print('[*] Waiting for CO2 data. Press Ctrl+C to exit')

    channel.basic_consume(
        queue=queue_name,
        on_message_callback=callback,
        auto_ack=True
    )
    try:
        channel.start_consuming()
    except KeyboardInterrupt:
        pass
