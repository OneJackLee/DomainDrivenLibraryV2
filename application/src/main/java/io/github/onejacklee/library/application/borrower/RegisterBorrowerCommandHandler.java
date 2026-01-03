package io.github.onejacklee.library.application.borrower;

import io.github.onejacklee.library.application.exception.BorrowerEmailAlreadyExistsException;
import io.github.onejacklee.library.common.application.IdGenerator;
import io.github.onejacklee.library.domain.borrower.Borrower;
import io.github.onejacklee.library.domain.borrower.BorrowerId;
import io.github.onejacklee.library.domain.borrower.BorrowerRepository;
import io.github.onejacklee.library.domain.borrower.EmailAddress;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RegisterBorrowerCommandHandler {

    private final BorrowerRepository borrowerRepository;
    private final IdGenerator idGenerator;

    @Transactional
    public BorrowerDto handle(RegisterBorrowerCommand command) {
        EmailAddress emailAddress = EmailAddress.create(command.emailAddress());

        if (borrowerRepository.existsByEmailAddress(emailAddress)) {
            throw new BorrowerEmailAlreadyExistsException(command.emailAddress());
        }

        BorrowerId borrowerId = BorrowerId.create(idGenerator.generate());
        Borrower borrower = Borrower.register(borrowerId, command.name(), emailAddress);
        borrowerRepository.save(borrower);

        return BorrowerDto.from(borrower);
    }
}
