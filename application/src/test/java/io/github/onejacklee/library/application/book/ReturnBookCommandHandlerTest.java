package io.github.onejacklee.library.application.book;

import io.github.onejacklee.library.application.exception.BookNotFoundException;
import io.github.onejacklee.library.domain.book.Book;
import io.github.onejacklee.library.domain.book.BookId;
import io.github.onejacklee.library.domain.book.BookRepository;
import io.github.onejacklee.library.domain.borrower.BorrowerId;
import io.github.onejacklee.library.domain.catalogentry.CatalogEntry;
import io.github.onejacklee.library.domain.catalogentry.CatalogEntryRepository;
import io.github.onejacklee.library.domain.catalogentry.Isbn;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReturnBookCommandHandler")
class ReturnBookCommandHandlerTest {

    private static final String BOOK_ID = "01ARZ3NDEKTSV4RRFFQ69G5FAV";
    private static final String BORROWER_ID = "01ARZ3NDEKTSV4RRFFQ69G5FAW";
    private static final String OTHER_BORROWER_ID = "01ARZ3NDEKTSV4RRFFQ69G5FAX";
    private static final String ISBN = "9780132350884";
    private static final String TITLE = "Clean Code";
    private static final String AUTHOR = "Robert C. Martin";

    @Mock
    private BookRepository bookRepository;

    @Mock
    private CatalogEntryRepository catalogEntryRepository;

    private ReturnBookCommandHandler handler;

    private Book book;
    private CatalogEntry catalogEntry;

    @BeforeEach
    void setUp() {
        handler = new ReturnBookCommandHandler(bookRepository, catalogEntryRepository);
        book = Book.register(BookId.create(BOOK_ID), Isbn.create(ISBN));
        catalogEntry = CatalogEntry.create(ISBN, TITLE, AUTHOR);
    }

    @Nested
    @DisplayName("when book is borrowed by the returning borrower")
    class WhenBookIsBorrowedByReturningBorrower {

        @BeforeEach
        void setUp() {
            book.borrow(BorrowerId.create(BORROWER_ID));
            when(bookRepository.findById(any(BookId.class))).thenReturn(Optional.of(book));
            when(catalogEntryRepository.findByIsbn(any(Isbn.class))).thenReturn(Optional.of(catalogEntry));
        }

        @Test
        @DisplayName("should return the book")
        void shouldReturnTheBook() {
            var command = new ReturnBookCommand(BOOK_ID, BORROWER_ID);

            BookDetailsDto result = handler.handle(command);

            assertThat(result.id()).isEqualTo(BOOK_ID);
            assertThat(result.available()).isTrue();
            assertThat(result.borrowerId()).isNull();
            assertThat(result.borrowedOn()).isNull();
        }

        @Test
        @DisplayName("should save the returned book")
        void shouldSaveTheReturnedBook() {
            var command = new ReturnBookCommand(BOOK_ID, BORROWER_ID);

            handler.handle(command);

            verify(bookRepository).save(book);
            assertThat(book.isAvailable()).isTrue();
        }

        @Test
        @DisplayName("should return book details with catalog info")
        void shouldReturnBookDetailsWithCatalogInfo() {
            var command = new ReturnBookCommand(BOOK_ID, BORROWER_ID);

            BookDetailsDto result = handler.handle(command);

            assertThat(result.isbn()).isEqualTo(ISBN);
            assertThat(result.title()).isEqualTo(TITLE);
            assertThat(result.author()).isEqualTo(AUTHOR);
        }
    }

    @Nested
    @DisplayName("when book does not exist")
    class WhenBookDoesNotExist {

        @BeforeEach
        void setUp() {
            when(bookRepository.findById(any(BookId.class))).thenReturn(Optional.empty());
        }

        @Test
        @DisplayName("should throw BookNotFoundException")
        void shouldThrowBookNotFoundException() {
            var command = new ReturnBookCommand(BOOK_ID, BORROWER_ID);

            assertThatThrownBy(() -> handler.handle(command))
                    .isInstanceOf(BookNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("when book is borrowed by different borrower")
    class WhenBookIsBorrowedByDifferentBorrower {

        @BeforeEach
        void setUp() {
            book.borrow(BorrowerId.create(OTHER_BORROWER_ID));
            when(bookRepository.findById(any(BookId.class))).thenReturn(Optional.of(book));
        }

        @Test
        @DisplayName("should throw IllegalStateException")
        void shouldThrowIllegalStateException() {
            var command = new ReturnBookCommand(BOOK_ID, BORROWER_ID);

            assertThatThrownBy(() -> handler.handle(command))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Book is not borrowed by borrower");
        }

        @Test
        @DisplayName("should not save book")
        void shouldNotSaveBook() {
            var command = new ReturnBookCommand(BOOK_ID, BORROWER_ID);

            try {
                handler.handle(command);
            } catch (IllegalStateException ignored) {
            }

            verify(bookRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("when book is not borrowed")
    class WhenBookIsNotBorrowed {

        @BeforeEach
        void setUp() {
            when(bookRepository.findById(any(BookId.class))).thenReturn(Optional.of(book));
        }

        @Test
        @DisplayName("should throw IllegalStateException")
        void shouldThrowIllegalStateException() {
            var command = new ReturnBookCommand(BOOK_ID, BORROWER_ID);

            assertThatThrownBy(() -> handler.handle(command))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Book is not borrowed by borrower");
        }
    }
}
