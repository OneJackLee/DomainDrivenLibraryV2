package io.github.onejacklee.library.infrastructure.persistence.repository;

import io.github.onejacklee.library.infrastructure.persistence.entity.BorrowerJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BorrowerJpaRepository extends JpaRepository<BorrowerJpaEntity, String> {

    Optional<BorrowerJpaEntity> findByEmailAddress(String emailAddress);

    boolean existsByEmailAddress(String emailAddress);
}
