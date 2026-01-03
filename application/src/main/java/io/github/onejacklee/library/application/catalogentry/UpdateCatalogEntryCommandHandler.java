package io.github.onejacklee.library.application.catalogentry;

import io.github.onejacklee.library.application.exception.CatalogEntryNotFoundException;
import io.github.onejacklee.library.domain.catalogentry.CatalogEntry;
import io.github.onejacklee.library.domain.catalogentry.CatalogEntryRepository;
import io.github.onejacklee.library.domain.catalogentry.Isbn;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateCatalogEntryCommandHandler {

    private final CatalogEntryRepository catalogEntryRepository;

    @Transactional
    public CatalogEntryDto handle(UpdateCatalogEntryCommand command) {
        Isbn isbn = Isbn.create(command.isbn());

        CatalogEntry catalogEntry = catalogEntryRepository.findByIsbn(isbn)
                .orElseThrow(() -> new CatalogEntryNotFoundException(command.isbn()));

        catalogEntry.updateTitle(command.title());
        catalogEntry.updateAuthor(command.author());
        catalogEntryRepository.save(catalogEntry);

        return CatalogEntryDto.from(catalogEntry);
    }
}
