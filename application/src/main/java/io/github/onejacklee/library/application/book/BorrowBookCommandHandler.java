package io.github.onejacklee.library.application.book;

import io.github.onejacklee.library.application.exception.BookNotFoundException;
import io.github.onejacklee.library.application.exception.BorrowerNotFoundException;
import io.github.onejacklee.library.application.exception.CatalogEntryNotFoundException;
import io.github.onejacklee.library.domain.book.Book;
import io.github.onejacklee.library.domain.book.BookId;
import io.github.onejacklee.library.domain.book.BookRepository;
import io.github.onejacklee.library.domain.borrower.BorrowerId;
import io.github.onejacklee.library.domain.borrower.BorrowerRepository;
import io.github.onejacklee.library.domain.catalogentry.CatalogEntry;
import io.github.onejacklee.library.domain.catalogentry.CatalogEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BorrowBookCommandHandler {

    private final BookRepository bookRepository;
    private final BorrowerRepository borrowerRepository;
    private final CatalogEntryRepository catalogEntryRepository;

    @Transactional
    public BookDetailsDto handle(BorrowBookCommand command) {
        BookId bookId = BookId.create(command.bookId());
        BorrowerId borrowerId = BorrowerId.create(command.borrowerId());

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(command.bookId()));

        borrowerRepository.findById(borrowerId)
                .orElseThrow(() -> new BorrowerNotFoundException(command.borrowerId()));

        book.borrow(borrowerId);
        bookRepository.save(book);

        CatalogEntry catalogEntry = catalogEntryRepository.findByIsbn(book.getIsbn())
                .orElseThrow(() -> new CatalogEntryNotFoundException(book.getIsbn().value()));

        return BookDetailsDto.from(book, catalogEntry);
    }
}
