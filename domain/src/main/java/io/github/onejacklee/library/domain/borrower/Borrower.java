package io.github.onejacklee.library.domain.borrower;

import io.github.onejacklee.library.common.domain.AggregateRoot;

import java.util.Objects;

public class Borrower extends AggregateRoot<BorrowerId> {

    private String name;
    private EmailAddress emailAddress;

    protected Borrower() {
        // For JPA
    }

    private Borrower(BorrowerId id, String name, EmailAddress emailAddress) {
        super(id);
        setName(name);
        this.emailAddress = Objects.requireNonNull(emailAddress, "Email address cannot be null");
    }

    public static Borrower register(BorrowerId id, String name, EmailAddress emailAddress) {
        return new Borrower(id, name, emailAddress);
    }

    public Borrower updateName(String name) {
        setName(name);
        return this;
    }

    public Borrower updateEmailAddress(EmailAddress emailAddress) {
        this.emailAddress = Objects.requireNonNull(emailAddress, "Email address cannot be null");
        return this;
    }

    private void setName(String name) {
        Objects.requireNonNull(name, "Name cannot be null");
        if (name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be blank");
        }
        this.name = name.trim();
    }

    public String getName() {
        return name;
    }

    public EmailAddress getEmailAddress() {
        return emailAddress;
    }

    // Package-private setters for JPA mapping
    void setNameInternal(String name) {
        this.name = name;
    }

    void setEmailAddressInternal(EmailAddress emailAddress) {
        this.emailAddress = emailAddress;
    }
}
