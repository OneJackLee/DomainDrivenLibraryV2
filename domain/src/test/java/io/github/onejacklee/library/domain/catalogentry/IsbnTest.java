package io.github.onejacklee.library.domain.catalogentry;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Isbn")
class IsbnTest {

    @Nested
    @DisplayName("creation")
    class Creation {

        @Test
        @DisplayName("should create valid ISBN-10")
        void shouldCreateValidIsbn10() {
            Isbn isbn = Isbn.create("0132350882");

            assertThat(isbn.value()).isEqualTo("0132350882");
        }

        @Test
        @DisplayName("should create valid ISBN-10 with X check digit")
        void shouldCreateValidIsbn10WithX() {
            Isbn isbn = Isbn.create("080442957X");

            assertThat(isbn.value()).isEqualTo("080442957X");
        }

        @Test
        @DisplayName("should create valid ISBN-13")
        void shouldCreateValidIsbn13() {
            Isbn isbn = Isbn.create("9780132350884");

            assertThat(isbn.value()).isEqualTo("9780132350884");
        }

        @Test
        @DisplayName("should normalize ISBN by removing hyphens")
        void shouldNormalizeIsbnByRemovingHyphens() {
            Isbn isbn = Isbn.create("978-0-13-235088-4");

            assertThat(isbn.value()).isEqualTo("9780132350884");
        }

        @Test
        @DisplayName("should normalize ISBN by removing spaces")
        void shouldNormalizeIsbnByRemovingSpaces() {
            Isbn isbn = Isbn.create("978 0 13 235088 4");

            assertThat(isbn.value()).isEqualTo("9780132350884");
        }

        @Test
        @DisplayName("should uppercase X check digit")
        void shouldUppercaseXCheckDigit() {
            Isbn isbn = Isbn.create("080442957x");

            assertThat(isbn.value()).isEqualTo("080442957X");
        }
    }

    @Nested
    @DisplayName("validation")
    class Validation {

        @Test
        @DisplayName("should throw when ISBN is null")
        void shouldThrowWhenNull() {
            assertThatThrownBy(() -> Isbn.create(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("ISBN cannot be null");
        }

        @ParameterizedTest
        @ValueSource(strings = {"", "123", "12345678901234", "abcdefghij", "123456789", "12345678901"})
        @DisplayName("should throw for invalid ISBN format")
        void shouldThrowForInvalidFormat(String invalidIsbn) {
            assertThatThrownBy(() -> Isbn.create(invalidIsbn))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("ISBN must be 10 or 13 digits");
        }
    }

    @Nested
    @DisplayName("tryParse")
    class TryParse {

        @Test
        @DisplayName("should return Optional with value for valid ISBN")
        void shouldReturnValueForValidIsbn() {
            var result = Isbn.tryParse("9780132350884");

            assertThat(result).isPresent();
            assertThat(result.get().value()).isEqualTo("9780132350884");
        }

        @Test
        @DisplayName("should return empty Optional for invalid ISBN")
        void shouldReturnEmptyForInvalidIsbn() {
            var result = Isbn.tryParse("invalid");

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("should return empty Optional for null")
        void shouldReturnEmptyForNull() {
            var result = Isbn.tryParse(null);

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("equality")
    class Equality {

        @Test
        @DisplayName("should be equal for same value")
        void shouldBeEqualForSameValue() {
            Isbn isbn1 = Isbn.create("9780132350884");
            Isbn isbn2 = Isbn.create("9780132350884");

            assertThat(isbn1).isEqualTo(isbn2);
            assertThat(isbn1.hashCode()).isEqualTo(isbn2.hashCode());
        }

        @Test
        @DisplayName("should be equal when normalized")
        void shouldBeEqualWhenNormalized() {
            Isbn isbn1 = Isbn.create("978-0-13-235088-4");
            Isbn isbn2 = Isbn.create("9780132350884");

            assertThat(isbn1).isEqualTo(isbn2);
        }

        @Test
        @DisplayName("should not be equal for different values")
        void shouldNotBeEqualForDifferentValues() {
            Isbn isbn1 = Isbn.create("9780132350884");
            Isbn isbn2 = Isbn.create("0132350882");

            assertThat(isbn1).isNotEqualTo(isbn2);
        }
    }

    @Test
    @DisplayName("toString should return value")
    void toStringShouldReturnValue() {
        Isbn isbn = Isbn.create("9780132350884");

        assertThat(isbn.toString()).isEqualTo("9780132350884");
    }
}
