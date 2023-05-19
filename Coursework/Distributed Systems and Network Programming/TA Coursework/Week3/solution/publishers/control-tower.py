import json

from pika import BlockingConnection, ConnectionParameters, PlainCredentials
from pika.exchange_type import ExchangeType

RMQ_HOST = 'localhost'
RMQ_USER = 'rabbit'
RMQ_PASS = '1234'
EXCHANGE_NAME = 'amq.topic'


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
            query = input("Enter query: ")
            if not query in ['average', 'current']:
                print("Invalid query")
                continue
            channel.basic_publish(
                exchange=EXCHANGE_NAME,
                routing_key=f"rep.{query}",
                body=json.dumps({'query': query})
            )
        except (EOFError, KeyboardInterrupt):
            connection.close()
            exit()
