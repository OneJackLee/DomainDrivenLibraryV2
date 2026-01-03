package io.github.onejacklee.library.domain.book;

import io.github.onejacklee.library.common.domain.AggregateRoot;
import io.github.onejacklee.library.domain.borrower.BorrowerId;
import io.github.onejacklee.library.domain.catalogentry.Isbn;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;
import java.util.Optional;

public class Book extends AggregateRoot<BookId> {

    private Isbn isbn;
    private BorrowerId borrowerId;
    private LocalDateTime borrowedOn;

    protected Book() {
        // For JPA
    }

    private Book(BookId id, Isbn isbn) {
        super(id);
        this.isbn = Objects.requireNonNull(isbn, "ISBN cannot be null");
    }

    public static Book register(BookId id, Isbn isbn) {
        return new Book(id, isbn);
    }

    public Book borrow(BorrowerId borrowerId) {
        return borrow(borrowerId, LocalDateTime.now(ZoneOffset.UTC));
    }

    public Book borrow(BorrowerId borrowerId, LocalDateTime borrowedOn) {
        if (!isAvailable()) {
            throw new IllegalStateException("Book is already borrowed");
        }
        this.borrowerId = Objects.requireNonNull(borrowerId, "BorrowerId cannot be null");
        this.borrowedOn = borrowedOn;
        return this;
    }

    public Book returnBook() {
        if (isAvailable()) {
            throw new IllegalStateException("Book is not borrowed");
        }
        this.borrowerId = null;
        this.borrowedOn = null;
        return this;
    }

    public boolean isAvailable() {
        return borrowerId == null;
    }

    public Isbn getIsbn() {
        return isbn;
    }

    public Optional<BorrowerId> getBorrowerId() {
        return Optional.ofNullable(borrowerId);
    }

    public Optional<LocalDateTime> getBorrowedOn() {
        return Optional.ofNullable(borrowedOn);
    }

    // Package-private setters for JPA mapping in infrastructure layer
    void setIsbn(Isbn isbn) {
        this.isbn = isbn;
    }

    void setBorrowerId(BorrowerId borrowerId) {
        this.borrowerId = borrowerId;
    }

    void setBorrowedOn(LocalDateTime borrowedOn) {
        this.borrowedOn = borrowedOn;
    }
}
