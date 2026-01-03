package io.github.onejacklee.library.application.book;

import io.github.onejacklee.library.application.exception.CatalogEntryConflictException;
import io.github.onejacklee.library.common.application.IdGenerator;
import io.github.onejacklee.library.domain.book.Book;
import io.github.onejacklee.library.domain.book.BookId;
import io.github.onejacklee.library.domain.book.BookRepository;
import io.github.onejacklee.library.domain.catalogentry.CatalogEntry;
import io.github.onejacklee.library.domain.catalogentry.CatalogEntryRepository;
import io.github.onejacklee.library.domain.catalogentry.Isbn;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RegisterBookCommandHandler {

    private final BookRepository bookRepository;
    private final CatalogEntryRepository catalogEntryRepository;
    private final IdGenerator idGenerator;

    @Transactional
    public BookDetailsDto handle(RegisterBookCommand command) {
        Isbn isbn = Isbn.create(command.isbn());

        CatalogEntry catalogEntry = catalogEntryRepository.findByIsbn(isbn)
                .map(existing -> validateAndReturn(existing, command))
                .orElseGet(() -> createCatalogEntry(isbn, command));

        BookId bookId = BookId.create(idGenerator.generate());
        Book book = Book.register(bookId, isbn);
        bookRepository.save(book);

        return BookDetailsDto.from(book, catalogEntry);
    }

    private CatalogEntry validateAndReturn(CatalogEntry existing, RegisterBookCommand command) {
        if (!existing.getTitle().equalsIgnoreCase(command.title()) ||
                !existing.getAuthor().equalsIgnoreCase(command.author())) {
            throw CatalogEntryConflictException.metadataMismatch(command.isbn());
        }
        return existing;
    }

    private CatalogEntry createCatalogEntry(Isbn isbn, RegisterBookCommand command) {
        CatalogEntry entry = CatalogEntry.create(isbn, command.title(), command.author());
        catalogEntryRepository.save(entry);
        return entry;
    }
}
