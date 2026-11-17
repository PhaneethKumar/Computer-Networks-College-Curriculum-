import socket

client_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
client_socket.connect(('127.0.0.1', 5000))
print("Connected to Server!\n")
print("--- AVAILABLE COMMANDS ---")
print("1. ECHO <message>")
print("2. PING")
print("3. TALK <message>")
print("4. exit")

while True:
    choice = input("\nEnter command prompt: ")
    if not choice:
        continue
        
    client_socket.sendall(choice.encode())
    
    if choice.lower() == 'exit':
        break
        
    response = client_socket.recv(1024).decode()
    print("Server Response:", response)
    print("-" * 30)

client_socket.close()