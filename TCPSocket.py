import socket

server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
server_socket.bind(('127.0.0.1', 5000))
server_socket.listen(1)
print("Server is waiting for connection...")

conn, addr = server_socket.accept()
print(f"Connected to client: {addr}")

while True:
    data = conn.recv(1024).decode()
    if not data or data.lower() == 'exit':
        break
    
    parts = data.split(" ", 1)
    cmd = parts[0].upper()
    payload = parts[1] if len(parts) > 1 else ""

    print(f"Received Command -> {cmd}: {payload}")

    if cmd == 'ECHO':
        conn.sendall(payload.encode())
    elif cmd == 'PING':
        conn.sendall(b"PONG")
    elif cmd == 'TALK':
        print("Message received. Type your reply:")
        reply = input("Server: ")
        conn.sendall(reply.encode())
    else:
        conn.sendall(b"Unknown Command")

conn.close()
server_socket.close()
print("Client disconnected.")