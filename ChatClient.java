import java.net.*;
import java.io.*;
public class ChatClient {
    public static void main(String[] args) throws Exception {
        try (Socket socket = new Socket("127.0.0.1", 5000);
             BufferedReader in = new BufferedReader(
                 new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(
                 socket.getOutputStream(), true);
             BufferedReader keyboard = new BufferedReader(
                 new InputStreamReader(System.in))) {
            System.out.println("Connected to Secure Chat Server");
            System.out.print("Username: ");
            String username = keyboard.readLine();
            System.out.print("Password: ");
            String password = keyboard.readLine();
            // Send encrypted login request
            out.println(AESUtil.encrypt(
                "AUTH|" + username + "|" + password));
            String response = AESUtil.decrypt(in.readLine());
            if (!response.equals("AUTH_OK")) {
                System.out.println("Login failed. Access denied.");
                return;
            }
            System.out.println("Login successful!");
            System.out.println("You can now start chatting.");
            while (true) {
                System.out.print("Enter message: ");
                String message = keyboard.readLine();
                if (message == null) break;
                String encrypted = AESUtil.encrypt(message);
                System.out.println("Encrypted message: " + encrypted);
                out.println(encrypted);
                if (message.equalsIgnoreCase("exit")) {
                    break;
                }
                String data = in.readLine();
                if (data == null) break;
                String reply = AESUtil.decrypt(data);
                System.out.println("Decrypted server reply: " + reply);
                if (reply.equalsIgnoreCase("exit")) {
                    break;
                }
            }
        }
        System.out.println("Client disconnected.");
    }
}
