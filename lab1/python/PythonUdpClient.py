import socket;

serverIP = "127.0.0.1"
serverPort = 9009
msg = "Żółta gęś"
msg_bytes = (300).to_bytes(4, byteorder='little')

print('PYTHON UDP CLIENT')
client = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
# client.sendto(bytes(msg, encoding='utf-8'), (serverIP, serverPort))
client.sendto(msg_bytes, (serverIP, serverPort))

serverPort = 9008
serverSocket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
serverSocket.bind(('', serverPort))
buff, address = serverSocket.recvfrom(1024)

print("Received:", int.from_bytes(buff, byteorder='big'))
