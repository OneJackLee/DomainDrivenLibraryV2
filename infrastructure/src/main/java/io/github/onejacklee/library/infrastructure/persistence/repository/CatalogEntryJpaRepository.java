package io.github.onejacklee.library.infrastructure.persistence.repository;

import io.github.onejacklee.library.infrastructure.persistence.entity.CatalogEntryJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CatalogEntryJpaRepository extends JpaRepository<CatalogEntryJpaEntity, String> {
}
