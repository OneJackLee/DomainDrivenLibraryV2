package io.github.onejacklee.library.domain.borrower;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Borrower")
class BorrowerTest {

    private static final BorrowerId BORROWER_ID = BorrowerId.create("01ARZ3NDEKTSV4RRFFQ69G5FAV");
    private static final String NAME = "John Doe";
    private static final EmailAddress EMAIL = EmailAddress.create("john.doe@example.com");

    @Nested
    @DisplayName("register")
    class Register {

        @Test
        @DisplayName("should create a new borrower")
        void shouldCreateNewBorrower() {
            Borrower borrower = Borrower.register(BORROWER_ID, NAME, EMAIL);

            assertThat(borrower.getId()).isEqualTo(BORROWER_ID);
            assertThat(borrower.getName()).isEqualTo(NAME);
            assertThat(borrower.getEmailAddress()).isEqualTo(EMAIL);
        }

        @Test
        @DisplayName("should trim name")
        void shouldTrimName() {
            Borrower borrower = Borrower.register(BORROWER_ID, "  John Doe  ", EMAIL);

            assertThat(borrower.getName()).isEqualTo("John Doe");
        }

        @Test
        @DisplayName("should throw when id is null")
        void shouldThrowWhenIdIsNull() {
            assertThatThrownBy(() -> Borrower.register(null, NAME, EMAIL))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("should throw when name is null")
        void shouldThrowWhenNameIsNull() {
            assertThatThrownBy(() -> Borrower.register(BORROWER_ID, null, EMAIL))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("Name cannot be null");
        }

        @Test
        @DisplayName("should throw when name is blank")
        void shouldThrowWhenNameIsBlank() {
            assertThatThrownBy(() -> Borrower.register(BORROWER_ID, "   ", EMAIL))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Name cannot be blank");
        }

        @Test
        @DisplayName("should throw when email is null")
        void shouldThrowWhenEmailIsNull() {
            assertThatThrownBy(() -> Borrower.register(BORROWER_ID, NAME, null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("Email address cannot be null");
        }
    }

    @Nested
    @DisplayName("updateName")
    class UpdateName {

        @Test
        @DisplayName("should update name")
        void shouldUpdateName() {
            Borrower borrower = Borrower.register(BORROWER_ID, NAME, EMAIL);

            borrower.updateName("Jane Doe");

            assertThat(borrower.getName()).isEqualTo("Jane Doe");
        }

        @Test
        @DisplayName("should trim new name")
        void shouldTrimNewName() {
            Borrower borrower = Borrower.register(BORROWER_ID, NAME, EMAIL);

            borrower.updateName("  Jane Doe  ");

            assertThat(borrower.getName()).isEqualTo("Jane Doe");
        }

        @Test
        @DisplayName("should throw when new name is null")
        void shouldThrowWhenNewNameIsNull() {
            Borrower borrower = Borrower.register(BORROWER_ID, NAME, EMAIL);

            assertThatThrownBy(() -> borrower.updateName(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("Name cannot be null");
        }

        @Test
        @DisplayName("should throw when new name is blank")
        void shouldThrowWhenNewNameIsBlank() {
            Borrower borrower = Borrower.register(BORROWER_ID, NAME, EMAIL);

            assertThatThrownBy(() -> borrower.updateName("   "))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Name cannot be blank");
        }

        @Test
        @DisplayName("should return self for fluent API")
        void shouldReturnSelfForFluentApi() {
            Borrower borrower = Borrower.register(BORROWER_ID, NAME, EMAIL);

            Borrower result = borrower.updateName("Jane Doe");

            assertThat(result).isSameAs(borrower);
        }
    }

    @Nested
    @DisplayName("updateEmailAddress")
    class UpdateEmailAddress {

        @Test
        @DisplayName("should update email address")
        void shouldUpdateEmailAddress() {
            Borrower borrower = Borrower.register(BORROWER_ID, NAME, EMAIL);
            EmailAddress newEmail = EmailAddress.create("jane.doe@example.com");

            borrower.updateEmailAddress(newEmail);

            assertThat(borrower.getEmailAddress()).isEqualTo(newEmail);
        }

        @Test
        @DisplayName("should throw when new email is null")
        void shouldThrowWhenNewEmailIsNull() {
            Borrower borrower = Borrower.register(BORROWER_ID, NAME, EMAIL);

            assertThatThrownBy(() -> borrower.updateEmailAddress(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("Email address cannot be null");
        }

        @Test
        @DisplayName("should return self for fluent API")
        void shouldReturnSelfForFluentApi() {
            Borrower borrower = Borrower.register(BORROWER_ID, NAME, EMAIL);
            EmailAddress newEmail = EmailAddress.create("jane.doe@example.com");

            Borrower result = borrower.updateEmailAddress(newEmail);

            assertThat(result).isSameAs(borrower);
        }
    }
}
