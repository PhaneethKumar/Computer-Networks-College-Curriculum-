import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;
public class AESUtil {
    private static final byte[] KEY =
        "0123456789ABCDEF".getBytes(
            java.nio.charset.StandardCharsets.UTF_8);
    public static String encrypt(String message) throws Exception {
        byte[] nonce = new byte[12];
        new SecureRandom().nextBytes(nonce);
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        SecretKeySpec key = new SecretKeySpec(KEY, "AES");
        cipher.init(Cipher.ENCRYPT_MODE, key,
                    new GCMParameterSpec(128, nonce));
        byte[] encrypted = cipher.doFinal(
            message.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        byte[] result = new byte[nonce.length + encrypted.length];
        System.arraycopy(nonce, 0, result, 0, nonce.length);
        System.arraycopy(encrypted, 0, result, nonce.length,
                         encrypted.length);
        return Base64.getEncoder().encodeToString(result);
    }
    public static String decrypt(String encoded) throws Exception {
        byte[] data = Base64.getDecoder().decode(encoded);
        if (data.length < 28) {
            throw new IllegalArgumentException("Invalid encrypted data");
        }
        byte[] nonce = new byte[12];
        byte[] encrypted = new byte[data.length - 12];
        System.arraycopy(data, 0, nonce, 0, 12);
        System.arraycopy(data, 12, encrypted, 0, encrypted.length);
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        SecretKeySpec key = new SecretKeySpec(KEY, "AES");
        cipher.init(Cipher.DECRYPT_MODE, key,
                    new GCMParameterSpec(128, nonce));
        byte[] decrypted = cipher.doFinal(encrypted);
        return new String(decrypted,
            java.nio.charset.StandardCharsets.UTF_8);
    }
}
