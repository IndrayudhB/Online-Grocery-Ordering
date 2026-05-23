package util;

import java.util.regex.Pattern;

/** Simple RFC-5322-ish email format validator. */
public final class EmailValidator {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private EmailValidator() {
        // utility
    }

    public static boolean isValid(String email) {
        return email != null && EMAIL_PATTERN.matcher(email.trim()).matches();
    }
}
