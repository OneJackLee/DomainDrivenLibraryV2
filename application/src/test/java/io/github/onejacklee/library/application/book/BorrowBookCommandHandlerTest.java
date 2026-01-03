package io.github.onejacklee.library.application.book;

import io.github.onejacklee.library.application.exception.BookNotFoundException;
import io.github.onejacklee.library.application.exception.BorrowerNotFoundException;
import io.github.onejacklee.library.domain.book.Book;
import io.github.onejacklee.library.domain.book.BookId;
import io.github.onejacklee.library.domain.book.BookRepository;
import io.github.onejacklee.library.domain.borrower.Borrower;
import io.github.onejacklee.library.domain.borrower.BorrowerId;
import io.github.onejacklee.library.domain.borrower.BorrowerRepository;
import io.github.onejacklee.library.domain.borrower.EmailAddress;
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
@DisplayName("BorrowBookCommandHandler")
class BorrowBookCommandHandlerTest {

    private static final String BOOK_ID = "01ARZ3NDEKTSV4RRFFQ69G5FAV";
    private static final String BORROWER_ID = "01ARZ3NDEKTSV4RRFFQ69G5FAW";
    private static final String ISBN = "9780132350884";
    private static final String TITLE = "Clean Code";
    private static final String AUTHOR = "Robert C. Martin";

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BorrowerRepository borrowerRepository;

    @Mock
    private CatalogEntryRepository catalogEntryRepository;

    private BorrowBookCommandHandler handler;

    private Book book;
    private Borrower borrower;
    private CatalogEntry catalogEntry;

    @BeforeEach
    void setUp() {
        handler = new BorrowBookCommandHandler(bookRepository, borrowerRepository, catalogEntryRepository);
        book = Book.register(BookId.create(BOOK_ID), Isbn.create(ISBN));
        borrower = Borrower.register(
                BorrowerId.create(BORROWER_ID),
                "John Doe",
                EmailAddress.create("john@example.com")
        );
        catalogEntry = CatalogEntry.create(ISBN, TITLE, AUTHOR);
    }

    @Nested
    @DisplayName("when book and borrower exist")
    class WhenBookAndBorrowerExist {

        @BeforeEach
        void setUp() {
            when(bookRepository.findById(any(BookId.class))).thenReturn(Optional.of(book));
            when(borrowerRepository.findById(any(BorrowerId.class))).thenReturn(Optional.of(borrower));
            when(catalogEntryRepository.findByIsbn(any(Isbn.class))).thenReturn(Optional.of(catalogEntry));
        }

        @Test
        @DisplayName("should borrow the book")
        void shouldBorrowTheBook() {
            var command = new BorrowBookCommand(BOOK_ID, BORROWER_ID);

            BookDetailsDto result = handler.handle(command);

            assertThat(result.id()).isEqualTo(BOOK_ID);
            assertThat(result.available()).isFalse();
            assertThat(result.borrowerId()).isEqualTo(BORROWER_ID);
            assertThat(result.borrowedOn()).isNotNull();
        }

        @Test
        @DisplayName("should save the borrowed book")
        void shouldSaveTheBorrowedBook() {
            var command = new BorrowBookCommand(BOOK_ID, BORROWER_ID);

            handler.handle(command);

            verify(bookRepository).save(book);
            assertThat(book.isAvailable()).isFalse();
        }

        @Test
        @DisplayName("should return book details with catalog info")
        void shouldReturnBookDetailsWithCatalogInfo() {
            var command = new BorrowBookCommand(BOOK_ID, BORROWER_ID);

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
            var command = new BorrowBookCommand(BOOK_ID, BORROWER_ID);

            assertThatThrownBy(() -> handler.handle(command))
                    .isInstanceOf(BookNotFoundException.class);
        }

        @Test
        @DisplayName("should not check borrower")
        void shouldNotCheckBorrower() {
            var command = new BorrowBookCommand(BOOK_ID, BORROWER_ID);

            try {
                handler.handle(command);
            } catch (BookNotFoundException ignored) {
            }

            verify(borrowerRepository, never()).findById(any());
        }
    }

    @Nested
    @DisplayName("when borrower does not exist")
    class WhenBorrowerDoesNotExist {

        @BeforeEach
        void setUp() {
            when(bookRepository.findById(any(BookId.class))).thenReturn(Optional.of(book));
            when(borrowerRepository.findById(any(BorrowerId.class))).thenReturn(Optional.empty());
        }

        @Test
        @DisplayName("should throw BorrowerNotFoundException")
        void shouldThrowBorrowerNotFoundException() {
            var command = new BorrowBookCommand(BOOK_ID, BORROWER_ID);

            assertThatThrownBy(() -> handler.handle(command))
                    .isInstanceOf(BorrowerNotFoundException.class);
        }

        @Test
        @DisplayName("should not save book")
        void shouldNotSaveBook() {
            var command = new BorrowBookCommand(BOOK_ID, BORROWER_ID);

            try {
                handler.handle(command);
            } catch (BorrowerNotFoundException ignored) {
            }

            verify(bookRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("when book is already borrowed")
    class WhenBookIsAlreadyBorrowed {

        @BeforeEach
        void setUp() {
            book.borrow(BorrowerId.create("01ARZ3NDEKTSV4RRFFQ69G5FAX"));
            when(bookRepository.findById(any(BookId.class))).thenReturn(Optional.of(book));
            when(borrowerRepository.findById(any(BorrowerId.class))).thenReturn(Optional.of(borrower));
        }

        @Test
        @DisplayName("should throw IllegalStateException")
        void shouldThrowIllegalStateException() {
            var command = new BorrowBookCommand(BOOK_ID, BORROWER_ID);

            assertThatThrownBy(() -> handler.handle(command))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Book is already borrowed");
        }
    }
}
