package io.github.onejacklee.library.application.book;

import io.github.onejacklee.library.domain.book.Book;
import io.github.onejacklee.library.domain.book.BookId;
import io.github.onejacklee.library.domain.book.BookRepository;
import io.github.onejacklee.library.domain.book.BookWithCatalog;
import io.github.onejacklee.library.domain.borrower.BorrowerId;
import io.github.onejacklee.library.domain.catalogentry.Isbn;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetAllBooksQueryHandler")
class GetAllBooksQueryHandlerTest {

    @Mock
    private BookRepository bookRepository;

    private GetAllBooksQueryHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GetAllBooksQueryHandler(bookRepository);
    }

    @Test
    @DisplayName("should return empty list when no books exist")
    void shouldReturnEmptyListWhenNoBooksExist() {
        when(bookRepository.findAllWithCatalog()).thenReturn(List.of());

        List<BookDetailsDto> result = handler.handle(new GetAllBooksQuery());

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("should return all books with catalog info")
    void shouldReturnAllBooksWithCatalogInfo() {
        Book book1 = Book.register(BookId.create("01ARZ3NDEKTSV4RRFFQ69G5FAV"), Isbn.create("9780132350884"));
        Book book2 = Book.register(BookId.create("01ARZ3NDEKTSV4RRFFQ69G5FAW"), Isbn.create("9780134685991"));
        book2.borrow(BorrowerId.create("01ARZ3NDEKTSV4RRFFQ69G5FAX"));

        List<BookWithCatalog> booksWithCatalog = List.of(
                BookWithCatalog.from(book1, "Clean Code", "Robert C. Martin"),
                BookWithCatalog.from(book2, "Clean Architecture", "Robert C. Martin")
        );
        when(bookRepository.findAllWithCatalog()).thenReturn(booksWithCatalog);

        List<BookDetailsDto> result = handler.handle(new GetAllBooksQuery());

        assertThat(result).hasSize(2);

        BookDetailsDto dto1 = result.get(0);
        assertThat(dto1.id()).isEqualTo("01ARZ3NDEKTSV4RRFFQ69G5FAV");
        assertThat(dto1.isbn()).isEqualTo("9780132350884");
        assertThat(dto1.title()).isEqualTo("Clean Code");
        assertThat(dto1.author()).isEqualTo("Robert C. Martin");
        assertThat(dto1.available()).isTrue();
        assertThat(dto1.borrowerId()).isNull();

        BookDetailsDto dto2 = result.get(1);
        assertThat(dto2.id()).isEqualTo("01ARZ3NDEKTSV4RRFFQ69G5FAW");
        assertThat(dto2.available()).isFalse();
        assertThat(dto2.borrowerId()).isEqualTo("01ARZ3NDEKTSV4RRFFQ69G5FAX");
    }

    @Test
    @DisplayName("should map available and borrowed books correctly")
    void shouldMapAvailableAndBorrowedBooksCorrectly() {
        Book availableBook = Book.register(BookId.create("01ARZ3NDEKTSV4RRFFQ69G5FAV"), Isbn.create("9780132350884"));
        Book borrowedBook = Book.register(BookId.create("01ARZ3NDEKTSV4RRFFQ69G5FAW"), Isbn.create("9780134685991"));
        borrowedBook.borrow(BorrowerId.create("01ARZ3NDEKTSV4RRFFQ69G5FAX"));

        List<BookWithCatalog> booksWithCatalog = List.of(
                BookWithCatalog.from(availableBook, "Clean Code", "Robert C. Martin"),
                BookWithCatalog.from(borrowedBook, "Clean Architecture", "Robert C. Martin")
        );
        when(bookRepository.findAllWithCatalog()).thenReturn(booksWithCatalog);

        List<BookDetailsDto> result = handler.handle(new GetAllBooksQuery());

        assertThat(result)
                .filteredOn(BookDetailsDto::available)
                .hasSize(1)
                .extracting(BookDetailsDto::id)
                .containsExactly("01ARZ3NDEKTSV4RRFFQ69G5FAV");

        assertThat(result)
                .filteredOn(dto -> !dto.available())
                .hasSize(1)
                .extracting(BookDetailsDto::id)
                .containsExactly("01ARZ3NDEKTSV4RRFFQ69G5FAW");
    }
}
