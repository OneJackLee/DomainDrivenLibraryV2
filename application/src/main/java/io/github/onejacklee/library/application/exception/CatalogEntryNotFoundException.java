package io.github.onejacklee.library.application.exception;

public class CatalogEntryNotFoundException extends RuntimeException {

    public CatalogEntryNotFoundException(String isbn) {
        super("Catalog entry not found with ISBN: " + isbn);
    }
}
