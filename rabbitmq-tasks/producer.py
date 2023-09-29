import pika
import time
from random import randint

from queue_types import QueueType


agency_name = input('Provide agency name:\n')

connection = pika.BlockingConnection(pika.ConnectionParameters(host='localhost'))
channel = connection.channel()

channel.exchange_declare(exchange='agency_transport_exchange', exchange_type='direct')

for queue_type in map(str, QueueType.__members__.values()):
    channel.queue_declare(queue=queue_type)
    channel.queue_bind(exchange='agency_transport_exchange', queue=queue_type, routing_key=queue_type)


for i in range(100):
    r = randint(1, 3)
    channel.basic_publish(exchange='agency_transport_exchange', routing_key=str(QueueType(r)),
                          body=(f"#{i} " + str(QueueType(r))).encode(),
                          properties=pika.BasicProperties(headers={'Agency': agency_name, 'Id': i}))
    print(str(QueueType(r)))

connection.close()
