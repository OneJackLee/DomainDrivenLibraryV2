package io.github.onejacklee.library.application.catalogentry;

public record UpdateCatalogEntryCommand(
        String isbn,
        String title,
        String author
) {
}
