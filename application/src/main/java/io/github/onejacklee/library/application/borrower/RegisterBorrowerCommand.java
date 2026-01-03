package io.github.onejacklee.library.application.borrower;

public record RegisterBorrowerCommand(
        String name,
        String emailAddress
) {
}
