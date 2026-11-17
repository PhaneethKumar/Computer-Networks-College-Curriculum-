import socket, os

HOST, PORT = "127.0.0.1", 8080
FOLDER = "uploads"
os.makedirs(FOLDER, exist_ok=True)

def send(c, status, ctype, data, extra=""):
    c.sendall((f"HTTP/1.1 {status}\r\nContent-Type: {ctype}\r\n"
               f"Content-Length: {len(data)}\r\n{extra}"
               f"Connection: close\r\n\r\n").encode() + data)

def page(msg=""):
    links = "".join(f'<li>{f} - <a href="/download/{f}">Download</a></li>'
                    for f in os.listdir(FOLDER))
    return f"""<html><body>
<h1>HTTP File Server</h1>
<form method="POST" action="/upload" enctype="multipart/form-data">
<input type="file" name="file" required>
<input type="submit" value="Upload">
</form>
<h3>{msg}</h3>
<ul>{links}</ul>
</body></html>""".encode()

def handle(c):
    req = b""
    while b"\r\n\r\n" not in req:              # read headers
        d = c.recv(4096)
        if not d: return
        req += d
    head, body = req.split(b"\r\n\r\n", 1)
    head = head.decode(errors="ignore")
    method, path, _ = head.split("\r\n")[0].split()

    if method == "GET" and path == "/":        # HOME
        send(c, "200 OK", "text/html", page())

    elif method == "POST" and path == "/upload":   # UPLOAD
        length = int([l.split(":")[1] for l in head.split("\r\n")
                      if l.lower().startswith("content-length")][0])
        while len(body) < length:              # read full body
            body += c.recv(4096)
        if b'filename="' not in body:
            send(c, "400 Bad Request", "text/html", page("Upload failed"))
        else:
            name = body.split(b'filename="')[1].split(b'"')[0].decode()
            name = os.path.basename(name)      # block path tricks
            data = body.split(b"\r\n\r\n", 1)[1].rsplit(b"\r\n--", 1)[0]
            open(os.path.join(FOLDER, name), "wb").write(data)
            send(c, "200 OK", "text/html", page("Uploaded successfully!"))

    elif method == "GET" and path.startswith("/download/"):   # DOWNLOAD
        name = os.path.basename(path[10:])
        fp = os.path.join(FOLDER, name)
        if os.path.exists(fp):
            send(c, "200 OK", "application/octet-stream", open(fp, "rb").read(),
                 f'Content-Disposition: attachment; filename="{name}"\r\n')
        else:
            send(c, "404 Not Found", "text/html", b"<h1>404 File Not Found</h1>")

    else:                                      # UNKNOWN
        send(c, "404 Not Found", "text/html", b"<h1>404 Page Not Found</h1>")

    c.close()

# SERVER
s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
s.bind((HOST, PORT))
s.listen(5)
print(f"Server running at http://{HOST}:{PORT}")

while True:
    c, addr = s.accept()
    handle(c)