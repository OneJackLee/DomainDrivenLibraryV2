package io.github.onejacklee.library.domain.book;

import io.github.onejacklee.library.domain.borrower.BorrowerId;
import io.github.onejacklee.library.domain.catalogentry.Isbn;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Book")
class BookTest {

    private static final BookId BOOK_ID = BookId.create("01ARZ3NDEKTSV4RRFFQ69G5FAV");
    private static final Isbn ISBN = Isbn.create("9780132350884");
    private static final BorrowerId BORROWER_ID = BorrowerId.create("01ARZ3NDEKTSV4RRFFQ69G5FAW");
    private static final BorrowerId OTHER_BORROWER_ID = BorrowerId.create("01ARZ3NDEKTSV4RRFFQ69G5FAX");

    @Nested
    @DisplayName("register")
    class Register {

        @Test
        @DisplayName("should create a new available book")
        void shouldCreateNewAvailableBook() {
            Book book = Book.register(BOOK_ID, ISBN);

            assertThat(book.getId()).isEqualTo(BOOK_ID);
            assertThat(book.getIsbn()).isEqualTo(ISBN);
            assertThat(book.isAvailable()).isTrue();
            assertThat(book.getBorrowerId()).isEmpty();
            assertThat(book.getBorrowedOn()).isEmpty();
        }

        @Test
        @DisplayName("should throw when id is null")
        void shouldThrowWhenIdIsNull() {
            assertThatThrownBy(() -> Book.register(null, ISBN))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("should throw when isbn is null")
        void shouldThrowWhenIsbnIsNull() {
            assertThatThrownBy(() -> Book.register(BOOK_ID, null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("ISBN cannot be null");
        }
    }

    @Nested
    @DisplayName("borrow")
    class Borrow {

        @Test
        @DisplayName("should borrow when book is available")
        void shouldBorrowWhenAvailable() {
            Book book = Book.register(BOOK_ID, ISBN);
            LocalDateTime borrowedOn = LocalDateTime.of(2024, 1, 15, 10, 30);

            book.borrow(BORROWER_ID, borrowedOn);

            assertThat(book.isAvailable()).isFalse();
            assertThat(book.getBorrowerId()).hasValue(BORROWER_ID);
            assertThat(book.getBorrowedOn()).hasValue(borrowedOn);
        }

        @Test
        @DisplayName("should set current timestamp when borrowing without explicit time")
        void shouldSetCurrentTimestampWhenBorrowingWithoutExplicitTime() {
            Book book = Book.register(BOOK_ID, ISBN);
            LocalDateTime before = LocalDateTime.now(java.time.ZoneOffset.UTC);

            book.borrow(BORROWER_ID);

            LocalDateTime after = LocalDateTime.now(java.time.ZoneOffset.UTC);
            assertThat(book.getBorrowedOn()).isPresent();
            assertThat(book.getBorrowedOn().get()).isBetween(before.minusSeconds(1), after.plusSeconds(1));
        }

        @Test
        @DisplayName("should throw when book is already borrowed")
        void shouldThrowWhenAlreadyBorrowed() {
            Book book = Book.register(BOOK_ID, ISBN);
            book.borrow(BORROWER_ID);

            assertThatThrownBy(() -> book.borrow(OTHER_BORROWER_ID))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Book is already borrowed");
        }

        @Test
        @DisplayName("should throw when borrowerId is null")
        void shouldThrowWhenBorrowerIdIsNull() {
            Book book = Book.register(BOOK_ID, ISBN);

            assertThatThrownBy(() -> book.borrow(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("BorrowerId cannot be null");
        }

        @Test
        @DisplayName("should return self for fluent API")
        void shouldReturnSelfForFluentApi() {
            Book book = Book.register(BOOK_ID, ISBN);

            Book result = book.borrow(BORROWER_ID);

            assertThat(result).isSameAs(book);
        }
    }

    @Nested
    @DisplayName("returnBook")
    class ReturnBook {

        @Test
        @DisplayName("should return borrowed book")
        void shouldReturnBorrowedBook() {
            Book book = Book.register(BOOK_ID, ISBN);
            book.borrow(BORROWER_ID);

            book.returnBook();

            assertThat(book.isAvailable()).isTrue();
            assertThat(book.getBorrowerId()).isEmpty();
            assertThat(book.getBorrowedOn()).isEmpty();
        }

        @Test
        @DisplayName("should throw when book is not borrowed")
        void shouldThrowWhenNotBorrowed() {
            Book book = Book.register(BOOK_ID, ISBN);

            assertThatThrownBy(() -> book.returnBook())
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Book is not borrowed");
        }

        @Test
        @DisplayName("should return self for fluent API")
        void shouldReturnSelfForFluentApi() {
            Book book = Book.register(BOOK_ID, ISBN);
            book.borrow(BORROWER_ID);

            Book result = book.returnBook();

            assertThat(result).isSameAs(book);
        }
    }

    @Nested
    @DisplayName("isAvailable")
    class IsAvailable {

        @Test
        @DisplayName("should return true for new book")
        void shouldReturnTrueForNewBook() {
            Book book = Book.register(BOOK_ID, ISBN);

            assertThat(book.isAvailable()).isTrue();
        }

        @Test
        @DisplayName("should return false for borrowed book")
        void shouldReturnFalseForBorrowedBook() {
            Book book = Book.register(BOOK_ID, ISBN);
            book.borrow(BORROWER_ID);

            assertThat(book.isAvailable()).isFalse();
        }

        @Test
        @DisplayName("should return true after book is returned")
        void shouldReturnTrueAfterBookIsReturned() {
            Book book = Book.register(BOOK_ID, ISBN);
            book.borrow(BORROWER_ID);
            book.returnBook();

            assertThat(book.isAvailable()).isTrue();
        }
    }
}
