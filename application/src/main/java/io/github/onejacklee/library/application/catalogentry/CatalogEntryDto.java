package io.github.onejacklee.library.application.catalogentry;

import io.github.onejacklee.library.domain.catalogentry.CatalogEntry;

public record CatalogEntryDto(
        String isbn,
        String title,
        String author
) {
    public static CatalogEntryDto from(CatalogEntry catalogEntry) {
        return new CatalogEntryDto(
                catalogEntry.getIsbn().value(),
                catalogEntry.getTitle(),
                catalogEntry.getAuthor()
        );
    }
}
