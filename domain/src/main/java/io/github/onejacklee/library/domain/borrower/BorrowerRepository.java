package io.github.onejacklee.library.domain.borrower;

import java.util.List;
import java.util.Optional;

public interface BorrowerRepository {

    void save(Borrower borrower);

    Optional<Borrower> findById(BorrowerId id);

    Optional<Borrower> findByEmailAddress(EmailAddress emailAddress);

    List<Borrower> findAll();

    boolean existsByEmailAddress(EmailAddress emailAddress);
}
