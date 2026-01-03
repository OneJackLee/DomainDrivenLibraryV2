package io.github.onejacklee.library.domain.borrower;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("BorrowerId")
class BorrowerIdTest {

    @Nested
    @DisplayName("creation")
    class Creation {

        @Test
        @DisplayName("should create with valid value")
        void shouldCreateWithValidValue() {
            BorrowerId borrowerId = BorrowerId.create("01ARZ3NDEKTSV4RRFFQ69G5FAV");

            assertThat(borrowerId.value()).isEqualTo("01ARZ3NDEKTSV4RRFFQ69G5FAV");
        }

        @Test
        @DisplayName("should throw when value is null")
        void shouldThrowWhenNull() {
            assertThatThrownBy(() -> BorrowerId.create(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("BorrowerId cannot be null");
        }

        @Test
        @DisplayName("should throw when value is empty")
        void shouldThrowWhenEmpty() {
            assertThatThrownBy(() -> BorrowerId.create(""))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("BorrowerId cannot be blank");
        }

        @Test
        @DisplayName("should throw when value is blank")
        void shouldThrowWhenBlank() {
            assertThatThrownBy(() -> BorrowerId.create("   "))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("BorrowerId cannot be blank");
        }
    }

    @Nested
    @DisplayName("equality")
    class Equality {

        @Test
        @DisplayName("should be equal for same value")
        void shouldBeEqualForSameValue() {
            BorrowerId borrowerId1 = BorrowerId.create("01ARZ3NDEKTSV4RRFFQ69G5FAV");
            BorrowerId borrowerId2 = BorrowerId.create("01ARZ3NDEKTSV4RRFFQ69G5FAV");

            assertThat(borrowerId1).isEqualTo(borrowerId2);
            assertThat(borrowerId1.hashCode()).isEqualTo(borrowerId2.hashCode());
        }

        @Test
        @DisplayName("should not be equal for different values")
        void shouldNotBeEqualForDifferentValues() {
            BorrowerId borrowerId1 = BorrowerId.create("01ARZ3NDEKTSV4RRFFQ69G5FAV");
            BorrowerId borrowerId2 = BorrowerId.create("01ARZ3NDEKTSV4RRFFQ69G5FAW");

            assertThat(borrowerId1).isNotEqualTo(borrowerId2);
        }
    }

    @Test
    @DisplayName("toString should return value")
    void toStringShouldReturnValue() {
        BorrowerId borrowerId = BorrowerId.create("01ARZ3NDEKTSV4RRFFQ69G5FAV");

        assertThat(borrowerId.toString()).isEqualTo("01ARZ3NDEKTSV4RRFFQ69G5FAV");
    }
}
