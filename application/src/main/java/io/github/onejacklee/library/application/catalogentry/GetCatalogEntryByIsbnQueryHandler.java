package io.github.onejacklee.library.application.catalogentry;

import io.github.onejacklee.library.application.exception.CatalogEntryNotFoundException;
import io.github.onejacklee.library.domain.catalogentry.CatalogEntryRepository;
import io.github.onejacklee.library.domain.catalogentry.Isbn;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetCatalogEntryByIsbnQueryHandler {

    private final CatalogEntryRepository catalogEntryRepository;

    @Transactional(readOnly = true)
    public CatalogEntryDto handle(GetCatalogEntryByIsbnQuery query) {
        Isbn isbn = Isbn.create(query.isbn());

        return catalogEntryRepository.findByIsbn(isbn)
                .map(CatalogEntryDto::from)
                .orElseThrow(() -> new CatalogEntryNotFoundException(query.isbn()));
    }
}
