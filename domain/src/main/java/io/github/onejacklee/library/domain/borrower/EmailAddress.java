package io.github.onejacklee.library.domain.borrower;

import java.util.Objects;
import java.util.regex.Pattern;

public record EmailAddress(String value) {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );

    public EmailAddress {
        Objects.requireNonNull(value, "Email address cannot be null");
        String normalized = value.toLowerCase().trim();
        if (!EMAIL_PATTERN.matcher(normalized).matches()) {
            throw new IllegalArgumentException("Invalid email address: " + value);
        }
        value = normalized;
    }

    public static EmailAddress create(String value) {
        return new EmailAddress(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
