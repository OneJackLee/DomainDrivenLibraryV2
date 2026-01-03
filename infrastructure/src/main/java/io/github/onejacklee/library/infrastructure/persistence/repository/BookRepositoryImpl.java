package io.github.onejacklee.library.infrastructure.persistence.repository;

import io.github.onejacklee.library.domain.book.Book;
import io.github.onejacklee.library.domain.book.BookId;
import io.github.onejacklee.library.domain.book.BookRepository;
import io.github.onejacklee.library.domain.book.BookWithCatalog;
import io.github.onejacklee.library.domain.borrower.BorrowerId;
import io.github.onejacklee.library.domain.catalogentry.Isbn;
import io.github.onejacklee.library.infrastructure.persistence.entity.BookJpaEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class BookRepositoryImpl implements BookRepository {

    private final BookJpaRepository jpaRepository;

    @Override
    public void save(Book book) {
        BookJpaEntity entity = toEntity(book);
        jpaRepository.save(entity);
    }

    @Override
    public Optional<Book> findById(BookId id) {
        return jpaRepository.findById(id.value())
                .map(this::toDomain);
    }

    @Override
    public List<Book> findAll() {
        return jpaRepository.findAll().stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<BookWithCatalog> findAllWithCatalog() {
        return jpaRepository.findAllWithCatalog().stream()
                .map(row -> {
                    BookJpaEntity bookEntity = (BookJpaEntity) row[0];
                    String title = (String) row[1];
                    String author = (String) row[2];

                    Book book = toDomain(bookEntity);
                    return BookWithCatalog.from(book, title, author);
                })
                .toList();
    }

    private BookJpaEntity toEntity(Book book) {
        return new BookJpaEntity(
                book.getId().value(),
                book.getIsbn().value(),
                book.getBorrowerId().map(BorrowerId::value).orElse(null),
                book.getBorrowedOn().orElse(null)
        );
    }

    private Book toDomain(BookJpaEntity entity) {
        BookId bookId = BookId.create(entity.getId());
        Isbn isbn = Isbn.create(entity.getIsbn());
        Book book = Book.register(bookId, isbn);

        if (entity.getBorrowerId() != null) {
            BorrowerId borrowerId = BorrowerId.create(entity.getBorrowerId());
            book.borrow(borrowerId, entity.getBorrowedOn());
        }

        return book;
    }
}
