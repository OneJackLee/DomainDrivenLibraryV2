package io.github.onejacklee.library.application.book;

public record ReturnBookCommand(
        String bookId,
        String borrowerId
) {
}
