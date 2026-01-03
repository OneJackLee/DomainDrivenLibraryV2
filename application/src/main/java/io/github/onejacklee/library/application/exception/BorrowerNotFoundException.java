package io.github.onejacklee.library.application.exception;

public class BorrowerNotFoundException extends RuntimeException {

    public BorrowerNotFoundException(String borrowerId) {
        super("Borrower not found with id: " + borrowerId);
    }
}
