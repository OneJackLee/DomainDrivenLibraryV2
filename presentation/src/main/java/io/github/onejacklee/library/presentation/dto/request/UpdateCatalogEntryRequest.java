package io.github.onejacklee.library.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateCatalogEntryRequest(
        @NotBlank(message = "Title is required")
        String title,

        @NotBlank(message = "Author is required")
        String author
) {
}
