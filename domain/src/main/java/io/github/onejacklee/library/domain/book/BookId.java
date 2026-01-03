package io.github.onejacklee.library.domain.book;

import java.util.Objects;

public record BookId(String value) {

    public BookId {
        Objects.requireNonNull(value, "BookId cannot be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("BookId cannot be blank");
        }
    }

    public static BookId create(String value) {
        return new BookId(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
