package io.github.onejacklee.library.domain.catalogentry;

import io.github.onejacklee.library.common.domain.AggregateRoot;

import java.util.Objects;

public class CatalogEntry extends AggregateRoot<Isbn> {

    private String title;
    private String author;

    protected CatalogEntry() {
        // For JPA
    }

    private CatalogEntry(Isbn isbn, String title, String author) {
        super(isbn);
        setTitle(title);
        setAuthor(author);
    }

    public static CatalogEntry create(Isbn isbn, String title, String author) {
        return new CatalogEntry(isbn, title, author);
    }

    public static CatalogEntry create(String isbn, String title, String author) {
        return new CatalogEntry(Isbn.create(isbn), title, author);
    }

    public CatalogEntry updateTitle(String title) {
        setTitle(title);
        return this;
    }

    public CatalogEntry updateAuthor(String author) {
        setAuthor(author);
        return this;
    }

    private void setTitle(String title) {
        Objects.requireNonNull(title, "Title cannot be null");
        if (title.isBlank()) {
            throw new IllegalArgumentException("Title cannot be blank");
        }
        this.title = title.trim();
    }

    private void setAuthor(String author) {
        Objects.requireNonNull(author, "Author cannot be null");
        if (author.isBlank()) {
            throw new IllegalArgumentException("Author cannot be blank");
        }
        this.author = author.trim();
    }

    public Isbn getIsbn() {
        return getId();
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    // Package-private setters for JPA mapping
    void setTitleInternal(String title) {
        this.title = title;
    }

    void setAuthorInternal(String author) {
        this.author = author;
    }
}
