package io.github.onejacklee.library.application.borrower;

import io.github.onejacklee.library.domain.borrower.Borrower;

public record BorrowerDto(
        String id,
        String name,
        String emailAddress
) {
    public static BorrowerDto from(Borrower borrower) {
        return new BorrowerDto(
                borrower.getId().value(),
                borrower.getName(),
                borrower.getEmailAddress().value()
        );
    }
}
