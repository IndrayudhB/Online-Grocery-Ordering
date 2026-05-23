package util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Password hashing helper. Uses SHA-256 with a static application salt.
 *
 * NOTE: For a production system you should use a per-user salt and a slow
 * KDF such as bcrypt/PBKDF2/Argon2. SHA-256 is used here only because the
 * project constraints disallow extra dependencies.
 */
public final class PasswordUtil {

    private static final String APP_SALT = "GROCERY_APP_STATIC_SALT_v1";

    private PasswordUtil() {
        // utility
    }

    public static String hash(String plain) {
        if (plain == null) {
            return null;
        }
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest((APP_SALT + plain).getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder(bytes.length * 2);
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException | java.io.UnsupportedEncodingException e) {
            throw new RuntimeException("Hashing not supported on this JVM", e);
        }
    }

    public static boolean matches(String plain, String hashed) {
        if (plain == null || hashed == null) {
            return false;
        }
        return hash(plain).equalsIgnoreCase(hashed);
    }
}
