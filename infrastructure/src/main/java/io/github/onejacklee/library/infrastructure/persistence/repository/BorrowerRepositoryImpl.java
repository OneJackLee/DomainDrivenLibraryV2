package io.github.onejacklee.library.infrastructure.persistence.repository;

import io.github.onejacklee.library.domain.borrower.Borrower;
import io.github.onejacklee.library.domain.borrower.BorrowerId;
import io.github.onejacklee.library.domain.borrower.BorrowerRepository;
import io.github.onejacklee.library.domain.borrower.EmailAddress;
import io.github.onejacklee.library.infrastructure.persistence.entity.BorrowerJpaEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class BorrowerRepositoryImpl implements BorrowerRepository {

    private final BorrowerJpaRepository jpaRepository;

    @Override
    public void save(Borrower borrower) {
        BorrowerJpaEntity entity = toEntity(borrower);
        jpaRepository.save(entity);
    }

    @Override
    public Optional<Borrower> findById(BorrowerId id) {
        return jpaRepository.findById(id.value())
                .map(this::toDomain);
    }

    @Override
    public Optional<Borrower> findByEmailAddress(EmailAddress emailAddress) {
        return jpaRepository.findByEmailAddress(emailAddress.value())
                .map(this::toDomain);
    }

    @Override
    public List<Borrower> findAll() {
        return jpaRepository.findAll().stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public boolean existsByEmailAddress(EmailAddress emailAddress) {
        return jpaRepository.existsByEmailAddress(emailAddress.value());
    }

    private BorrowerJpaEntity toEntity(Borrower borrower) {
        return new BorrowerJpaEntity(
                borrower.getId().value(),
                borrower.getName(),
                borrower.getEmailAddress().value()
        );
    }

    private Borrower toDomain(BorrowerJpaEntity entity) {
        BorrowerId id = BorrowerId.create(entity.getId());
        EmailAddress email = EmailAddress.create(entity.getEmailAddress());
        return Borrower.register(id, entity.getName(), email);
    }
}
