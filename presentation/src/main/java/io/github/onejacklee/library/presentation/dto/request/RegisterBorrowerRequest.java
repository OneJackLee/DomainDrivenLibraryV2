package io.github.onejacklee.library.presentation.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterBorrowerRequest(
        @NotBlank(message = "Name is required")
        String name,

        @NotBlank(message = "Email address is required")
        @Email(message = "Invalid email address format")
        String emailAddress
) {
}
