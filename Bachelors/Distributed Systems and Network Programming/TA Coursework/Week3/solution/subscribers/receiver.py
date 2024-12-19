import json

from pika import BlockingConnection, ConnectionParameters, PlainCredentials
from pika.exchange_type import ExchangeType

RMQ_HOST = 'localhost'
RMQ_USER = 'rabbit'
RMQ_PASS = '1234'
EXCHANGE_NAME = 'amq.topic'
ROUTING_KEY = 'co2.*'


def callback(channel, method, properties, body):
    body = body.decode('utf-8')
    with open('receiver.log', 'a+') as f:
        f.write(f"{body}\n")
    message = json.loads(body)
    status = 'OK' if int(message['value']) <= 500 else 'WARNING'
    print(f"{message['time']}: {status}")


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
