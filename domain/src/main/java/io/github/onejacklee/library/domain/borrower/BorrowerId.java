package io.github.onejacklee.library.domain.borrower;

import java.util.Objects;

public record BorrowerId(String value) {

    public BorrowerId {
        Objects.requireNonNull(value, "BorrowerId cannot be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("BorrowerId cannot be blank");
        }
    }

    public static BorrowerId create(String value) {
        return new BorrowerId(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
