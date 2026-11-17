import java.net.*;
import java.io.*;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
public class ChatServer {
    private static final Map<String, String> USERS = new HashMap<>();
    static {
        // Demo accounts: store SHA-256 password hashes
        USERS.put("student",
            "703b0a3d6ad75b649a28adde7d83c6251da457549263bc7ff45ec709b0a8448b");
        USERS.put("admin",
            "240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9");
    }
    private static boolean authenticate(String username, String password)
            throws Exception {
        String storedHash = USERS.get(username);
        if (storedHash == null) {
            return false;
        }
        byte[] hash = MessageDigest.getInstance("SHA-256")
            .digest(password.getBytes(StandardCharsets.UTF_8));
        StringBuilder actual = new StringBuilder();
        for (byte b : hash) {
            actual.append(String.format("%02x", b & 0xff));
        }
        return MessageDigest.isEqual(
            storedHash.getBytes(StandardCharsets.UTF_8),
            actual.toString().getBytes(StandardCharsets.UTF_8));
    }
    public static void main(String[] args) throws Exception {
        try (ServerSocket server = new ServerSocket(5000)) {
            System.out.println("Secure Chat Server started.");
            System.out.println("Waiting for client...");
            try (Socket socket = server.accept();
                 BufferedReader in = new BufferedReader(
                     new InputStreamReader(socket.getInputStream()));
                 PrintWriter out = new PrintWriter(
                     socket.getOutputStream(), true)) {
                System.out.println("Client connected.");
                String username = "unknown";
                try {
                    String request = AESUtil.decrypt(in.readLine());
                    String[] parts = request.split("\\|", 3);
                    if (parts.length != 3 || !parts[0].equals("AUTH")) {
                        out.println(AESUtil.encrypt("AUTH_FAIL"));
                        MessageLogger.log(username, "Invalid login request");
                        return;
                    }
                    username = parts[1];
                    boolean valid = authenticate(username, parts[2]);
                    if (!valid) {
                        out.println(AESUtil.encrypt("AUTH_FAIL"));
                        MessageLogger.log(username, "Login failed");
                        System.out.println("Authentication failed.");
                        return;
                    }

                    out.println(AESUtil.encrypt("AUTH_OK"));
                    MessageLogger.log(username, "Login successful");
                    System.out.println(username + " authenticated.");
                    while (true) {
                        String encrypted = in.readLine();
                        if (encrypted == null) break;
                        String message = AESUtil.decrypt(encrypted);
                        System.out.println("Encrypted: " + encrypted);
                        System.out.println(username + ": " + message);
                        if (message.equalsIgnoreCase("exit")) {
                            MessageLogger.log(username, "Logout");
                            break;
                        }
                        MessageLogger.log(username, "Message: " + message);
                        System.out.print("Enter reply: ");
                        BufferedReader keyboard = new BufferedReader(
                            new InputStreamReader(System.in));
                        String reply = keyboard.readLine();
                        if (reply == null) break;
                        out.println(AESUtil.encrypt(reply));
                        MessageLogger.log("server", "Reply to " +
                            username + ": " + reply);
                        if (reply.equalsIgnoreCase("exit")) {
                            MessageLogger.log(username, "Chat ended");
                            break;
                        }
                    }
                } catch (Exception e) {
                    MessageLogger.log(username, "Connection or message error");
                    System.out.println("Connection ended: " + e.getMessage());
                }
            }
        }
        System.out.println("Server stopped.");
    }
}
