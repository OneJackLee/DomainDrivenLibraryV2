package io.github.onejacklee.library.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ReturnBookRequest(
        @NotBlank(message = "Borrower ID is required")
        String borrowerId
) {
}
