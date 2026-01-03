package io.github.onejacklee.library.presentation.dto.response;

import io.github.onejacklee.library.application.borrower.BorrowerDto;

public record BorrowerResponse(
        String id,
        String name,
        String emailAddress
) {
    public static BorrowerResponse from(BorrowerDto dto) {
        return new BorrowerResponse(
                dto.id(),
                dto.name(),
                dto.emailAddress()
        );
    }
}
