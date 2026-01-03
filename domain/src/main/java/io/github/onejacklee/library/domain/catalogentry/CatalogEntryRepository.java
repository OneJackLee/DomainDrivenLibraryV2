package io.github.onejacklee.library.domain.catalogentry;

import java.util.Optional;

public interface CatalogEntryRepository {

    void save(CatalogEntry catalogEntry);

    Optional<CatalogEntry> findByIsbn(Isbn isbn);

    boolean existsByIsbn(Isbn isbn);
}
