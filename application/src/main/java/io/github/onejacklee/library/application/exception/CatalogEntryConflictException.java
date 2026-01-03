package io.github.onejacklee.library.application.exception;

public class CatalogEntryConflictException extends RuntimeException {

    public CatalogEntryConflictException(String message) {
        super(message);
    }

    public static CatalogEntryConflictException metadataMismatch(String isbn) {
        return new CatalogEntryConflictException(
                "ISBN " + isbn + " exists with different title/author metadata"
        );
    }
}
