import json
from datetime import datetime

from pika import BlockingConnection, ConnectionParameters, PlainCredentials
from pika.exchange_type import ExchangeType

RMQ_HOST = 'localhost'
RMQ_USER = 'rabbit'
RMQ_PASS = '1234'
EXCHANGE_NAME = 'amq.topic'
ROUTING_KEY = 'co2.sensor'

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
    while True:
        try:
            message = {
                'time': datetime.now().strftime('%Y-%m-%d %H:%M:%S'),
                'value': input('Enter CO2 Level: ')
            }
            channel.basic_publish(
                exchange=EXCHANGE_NAME,
                routing_key=ROUTING_KEY,
                body=json.dumps(message)
            )
        except (EOFError, KeyboardInterrupt):
            connection.close()
            break
