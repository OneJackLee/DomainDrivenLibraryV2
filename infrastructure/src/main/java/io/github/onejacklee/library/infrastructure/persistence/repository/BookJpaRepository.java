package io.github.onejacklee.library.infrastructure.persistence.repository;

import io.github.onejacklee.library.infrastructure.persistence.entity.BookJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BookJpaRepository extends JpaRepository<BookJpaEntity, String> {

    @Query("""
            SELECT b, c.title, c.author
            FROM BookJpaEntity b
            JOIN CatalogEntryJpaEntity c ON b.isbn = c.isbn
            """)
    List<Object[]> findAllWithCatalog();
}
