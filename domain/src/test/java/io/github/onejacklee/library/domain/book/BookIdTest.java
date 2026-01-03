package io.github.onejacklee.library.domain.book;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("BookId")
class BookIdTest {

    @Nested
    @DisplayName("creation")
    class Creation {

        @Test
        @DisplayName("should create with valid value")
        void shouldCreateWithValidValue() {
            BookId bookId = BookId.create("01ARZ3NDEKTSV4RRFFQ69G5FAV");

            assertThat(bookId.value()).isEqualTo("01ARZ3NDEKTSV4RRFFQ69G5FAV");
        }

        @Test
        @DisplayName("should throw when value is null")
        void shouldThrowWhenNull() {
            assertThatThrownBy(() -> BookId.create(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("BookId cannot be null");
        }

        @Test
        @DisplayName("should throw when value is empty")
        void shouldThrowWhenEmpty() {
            assertThatThrownBy(() -> BookId.create(""))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("BookId cannot be blank");
        }

        @Test
        @DisplayName("should throw when value is blank")
        void shouldThrowWhenBlank() {
            assertThatThrownBy(() -> BookId.create("   "))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("BookId cannot be blank");
        }
    }

    @Nested
    @DisplayName("equality")
    class Equality {

        @Test
        @DisplayName("should be equal for same value")
        void shouldBeEqualForSameValue() {
            BookId bookId1 = BookId.create("01ARZ3NDEKTSV4RRFFQ69G5FAV");
            BookId bookId2 = BookId.create("01ARZ3NDEKTSV4RRFFQ69G5FAV");

            assertThat(bookId1).isEqualTo(bookId2);
            assertThat(bookId1.hashCode()).isEqualTo(bookId2.hashCode());
        }

        @Test
        @DisplayName("should not be equal for different values")
        void shouldNotBeEqualForDifferentValues() {
            BookId bookId1 = BookId.create("01ARZ3NDEKTSV4RRFFQ69G5FAV");
            BookId bookId2 = BookId.create("01ARZ3NDEKTSV4RRFFQ69G5FAW");

            assertThat(bookId1).isNotEqualTo(bookId2);
        }
    }

    @Test
    @DisplayName("toString should return value")
    void toStringShouldReturnValue() {
        BookId bookId = BookId.create("01ARZ3NDEKTSV4RRFFQ69G5FAV");

        assertThat(bookId.toString()).isEqualTo("01ARZ3NDEKTSV4RRFFQ69G5FAV");
    }
}
