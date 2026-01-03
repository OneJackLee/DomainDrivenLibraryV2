package io.github.onejacklee.library.application.book;

public record BorrowBookCommand(
        String bookId,
        String borrowerId
) {
}
