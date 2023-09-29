import pika
import time

from queue_types import QueueType


def transport_callback(ch, method, properties, body):
    print(f"Received {body.decode()}")
    headers = properties.headers

    try:
        print(f"From: {headers['Agency']}; Id: {headers['Id']}")
    except KeyError:
        print("Missing headers in request")

    ch.basic_ack(delivery_tag=method.delivery_tag)


PROMPT = """Select 2 service types:
1 - people transport
2 - cargo transport
3 - satellite transport"""
if __name__ == "__main__":
    print(PROMPT)
    t1, t2 = map(int, input('> ').split())

    connection = pika.BlockingConnection(pika.ConnectionParameters('localhost'))
    channel = connection.channel()
    channel.basic_qos(prefetch_count=1)

    channel.queue_declare(queue=str(QueueType(t1)))
    channel.queue_declare(queue=str(QueueType(t2)))

    channel.basic_consume(queue=str(QueueType(t1)), auto_ack=False, on_message_callback=transport_callback)
    channel.basic_consume(queue=str(QueueType(t2)), auto_ack=False, on_message_callback=transport_callback)

    channel.start_consuming()
