package io.github.onejacklee.library.application.book;

import io.github.onejacklee.library.domain.book.Book;
import io.github.onejacklee.library.domain.book.BookWithCatalog;
import io.github.onejacklee.library.domain.catalogentry.CatalogEntry;

import java.time.LocalDateTime;

public record BookDetailsDto(
        String id,
        String isbn,
        String title,
        String author,
        boolean available,
        String borrowerId,
        LocalDateTime borrowedOn
) {
    public static BookDetailsDto from(Book book, CatalogEntry catalogEntry) {
        return new BookDetailsDto(
                book.getId().value(),
                book.getIsbn().value(),
                catalogEntry.getTitle(),
                catalogEntry.getAuthor(),
                book.isAvailable(),
                book.getBorrowerId().map(id -> id.value()).orElse(null),
                book.getBorrowedOn().orElse(null)
        );
    }

    public static BookDetailsDto from(BookWithCatalog bookWithCatalog) {
        return new BookDetailsDto(
                bookWithCatalog.id().value(),
                bookWithCatalog.isbn().value(),
                bookWithCatalog.title(),
                bookWithCatalog.author(),
                bookWithCatalog.available(),
                bookWithCatalog.borrowerId().map(id -> id.value()).orElse(null),
                bookWithCatalog.borrowedOn().orElse(null)
        );
    }
}
