package io.github.onejacklee.library.domain.book;

import io.github.onejacklee.library.domain.borrower.BorrowerId;
import io.github.onejacklee.library.domain.catalogentry.Isbn;

import java.time.LocalDateTime;
import java.util.Optional;

public record BookWithCatalog(
        BookId id,
        Isbn isbn,
        String title,
        String author,
        boolean available,
        Optional<BorrowerId> borrowerId,
        Optional<LocalDateTime> borrowedOn
) {
    public static BookWithCatalog from(Book book, String title, String author) {
        return new BookWithCatalog(
                book.getId(),
                book.getIsbn(),
                title,
                author,
                book.isAvailable(),
                book.getBorrowerId(),
                book.getBorrowedOn()
        );
    }
}
