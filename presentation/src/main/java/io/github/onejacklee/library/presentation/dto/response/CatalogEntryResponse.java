package io.github.onejacklee.library.presentation.dto.response;

import io.github.onejacklee.library.application.catalogentry.CatalogEntryDto;

public record CatalogEntryResponse(
        String isbn,
        String title,
        String author
) {
    public static CatalogEntryResponse from(CatalogEntryDto dto) {
        return new CatalogEntryResponse(
                dto.isbn(),
                dto.title(),
                dto.author()
        );
    }
}
