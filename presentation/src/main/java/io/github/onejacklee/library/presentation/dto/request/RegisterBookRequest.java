package io.github.onejacklee.library.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;

public record RegisterBookRequest(
        @NotBlank(message = "ISBN is required")
        String isbn,

        @NotBlank(message = "Title is required")
        String title,

        @NotBlank(message = "Author is required")
        String author
) {
}
