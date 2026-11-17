import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
public class MessageLogger {
    public static synchronized void log(
            String username, String event) {
        String time = LocalDateTime.now().format(
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        try (PrintWriter out = new PrintWriter(
                new FileWriter("chat_log.txt", true))) {
            out.println(time + " | " + username + " | " + event);
        } catch (Exception e) {
            System.out.println("Unable to write log: " + e.getMessage());
        }
    }
}
