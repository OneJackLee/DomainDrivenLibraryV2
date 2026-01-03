package io.github.onejacklee.library.domain.book;

import java.util.List;
import java.util.Optional;

public interface BookRepository {

    void save(Book book);

    Optional<Book> findById(BookId id);

    List<Book> findAll();

    List<BookWithCatalog> findAllWithCatalog();
}
