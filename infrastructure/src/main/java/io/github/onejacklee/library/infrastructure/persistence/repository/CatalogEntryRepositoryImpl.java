package io.github.onejacklee.library.infrastructure.persistence.repository;

import io.github.onejacklee.library.domain.catalogentry.CatalogEntry;
import io.github.onejacklee.library.domain.catalogentry.CatalogEntryRepository;
import io.github.onejacklee.library.domain.catalogentry.Isbn;
import io.github.onejacklee.library.infrastructure.persistence.entity.CatalogEntryJpaEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CatalogEntryRepositoryImpl implements CatalogEntryRepository {

    private final CatalogEntryJpaRepository jpaRepository;

    @Override
    public void save(CatalogEntry catalogEntry) {
        CatalogEntryJpaEntity entity = toEntity(catalogEntry);
        jpaRepository.save(entity);
    }

    @Override
    public Optional<CatalogEntry> findByIsbn(Isbn isbn) {
        return jpaRepository.findById(isbn.value())
                .map(this::toDomain);
    }

    @Override
    public boolean existsByIsbn(Isbn isbn) {
        return jpaRepository.existsById(isbn.value());
    }

    private CatalogEntryJpaEntity toEntity(CatalogEntry catalogEntry) {
        return new CatalogEntryJpaEntity(
                catalogEntry.getIsbn().value(),
                catalogEntry.getTitle(),
                catalogEntry.getAuthor()
        );
    }

    private CatalogEntry toDomain(CatalogEntryJpaEntity entity) {
        return CatalogEntry.create(entity.getIsbn(), entity.getTitle(), entity.getAuthor());
    }
}
