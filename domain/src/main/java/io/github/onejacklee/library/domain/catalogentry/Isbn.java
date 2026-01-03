package io.github.onejacklee.library.domain.catalogentry;

import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

public record Isbn(String value) {

    private static final Pattern ISBN_10 = Pattern.compile("^\\d{9}[\\dX]$");
    private static final Pattern ISBN_13 = Pattern.compile("^\\d{13}$");

    public Isbn {
        Objects.requireNonNull(value, "ISBN cannot be null");
        value = normalize(value);
        if (!isValid(value)) {
            throw new IllegalArgumentException("ISBN must be 10 or 13 digits. Got: " + value);
        }
    }

    public static Isbn create(String value) {
        return new Isbn(value);
    }

    public static Optional<Isbn> tryParse(String value) {
        try {
            return Optional.of(new Isbn(value));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private static String normalize(String value) {
        return value.replace("-", "").replace(" ", "").toUpperCase();
    }

    private static boolean isValid(String value) {
        return ISBN_10.matcher(value).matches() || ISBN_13.matcher(value).matches();
    }

    @Override
    public String toString() {
        return value;
    }
}
