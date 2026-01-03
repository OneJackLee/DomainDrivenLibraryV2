package io.github.onejacklee.library.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "books")
public class BookJpaEntity {

    @Id
    @Column(name = "id", length = 26)
    private String id;

    @Column(name = "isbn", nullable = false, length = 13)
    private String isbn;

    @Column(name = "borrower_id", length = 26)
    private String borrowerId;

    @Column(name = "borrowed_on")
    private LocalDateTime borrowedOn;

    protected BookJpaEntity() {
    }

    public BookJpaEntity(String id, String isbn, String borrowerId, LocalDateTime borrowedOn) {
        this.id = id;
        this.isbn = isbn;
        this.borrowerId = borrowerId;
        this.borrowedOn = borrowedOn;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getBorrowerId() {
        return borrowerId;
    }

    public void setBorrowerId(String borrowerId) {
        this.borrowerId = borrowerId;
    }

    public LocalDateTime getBorrowedOn() {
        return borrowedOn;
    }

    public void setBorrowedOn(LocalDateTime borrowedOn) {
        this.borrowedOn = borrowedOn;
    }
}
