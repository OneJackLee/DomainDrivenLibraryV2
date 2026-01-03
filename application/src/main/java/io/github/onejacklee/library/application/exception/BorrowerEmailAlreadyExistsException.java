package io.github.onejacklee.library.application.exception;

public class BorrowerEmailAlreadyExistsException extends RuntimeException {

    public BorrowerEmailAlreadyExistsException(String email) {
        super("A borrower with email " + email + " already exists");
    }
}
