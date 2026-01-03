package io.github.onejacklee.library.domain.catalogentry;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("CatalogEntry")
class CatalogEntryTest {

    private static final Isbn ISBN = Isbn.create("9780132350884");
    private static final String TITLE = "Clean Code";
    private static final String AUTHOR = "Robert C. Martin";

    @Nested
    @DisplayName("create with Isbn")
    class CreateWithIsbn {

        @Test
        @DisplayName("should create a new catalog entry")
        void shouldCreateNewCatalogEntry() {
            CatalogEntry entry = CatalogEntry.create(ISBN, TITLE, AUTHOR);

            assertThat(entry.getId()).isEqualTo(ISBN);
            assertThat(entry.getIsbn()).isEqualTo(ISBN);
            assertThat(entry.getTitle()).isEqualTo(TITLE);
            assertThat(entry.getAuthor()).isEqualTo(AUTHOR);
        }

        @Test
        @DisplayName("should trim title")
        void shouldTrimTitle() {
            CatalogEntry entry = CatalogEntry.create(ISBN, "  Clean Code  ", AUTHOR);

            assertThat(entry.getTitle()).isEqualTo("Clean Code");
        }

        @Test
        @DisplayName("should trim author")
        void shouldTrimAuthor() {
            CatalogEntry entry = CatalogEntry.create(ISBN, TITLE, "  Robert C. Martin  ");

            assertThat(entry.getAuthor()).isEqualTo("Robert C. Martin");
        }

        @Test
        @DisplayName("should throw when isbn is null")
        void shouldThrowWhenIsbnIsNull() {
            assertThatThrownBy(() -> CatalogEntry.create((Isbn) null, TITLE, AUTHOR))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("should throw when title is null")
        void shouldThrowWhenTitleIsNull() {
            assertThatThrownBy(() -> CatalogEntry.create(ISBN, null, AUTHOR))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("Title cannot be null");
        }

        @Test
        @DisplayName("should throw when title is blank")
        void shouldThrowWhenTitleIsBlank() {
            assertThatThrownBy(() -> CatalogEntry.create(ISBN, "   ", AUTHOR))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Title cannot be blank");
        }

        @Test
        @DisplayName("should throw when author is null")
        void shouldThrowWhenAuthorIsNull() {
            assertThatThrownBy(() -> CatalogEntry.create(ISBN, TITLE, null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("Author cannot be null");
        }

        @Test
        @DisplayName("should throw when author is blank")
        void shouldThrowWhenAuthorIsBlank() {
            assertThatThrownBy(() -> CatalogEntry.create(ISBN, TITLE, "   "))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Author cannot be blank");
        }
    }

    @Nested
    @DisplayName("create with String ISBN")
    class CreateWithStringIsbn {

        @Test
        @DisplayName("should create a new catalog entry from string ISBN")
        void shouldCreateFromStringIsbn() {
            CatalogEntry entry = CatalogEntry.create("9780132350884", TITLE, AUTHOR);

            assertThat(entry.getIsbn().value()).isEqualTo("9780132350884");
            assertThat(entry.getTitle()).isEqualTo(TITLE);
            assertThat(entry.getAuthor()).isEqualTo(AUTHOR);
        }

        @Test
        @DisplayName("should throw for invalid ISBN string")
        void shouldThrowForInvalidIsbn() {
            assertThatThrownBy(() -> CatalogEntry.create("invalid", TITLE, AUTHOR))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("ISBN must be 10 or 13 digits");
        }
    }

    @Nested
    @DisplayName("updateTitle")
    class UpdateTitle {

        @Test
        @DisplayName("should update title")
        void shouldUpdateTitle() {
            CatalogEntry entry = CatalogEntry.create(ISBN, TITLE, AUTHOR);

            entry.updateTitle("Clean Architecture");

            assertThat(entry.getTitle()).isEqualTo("Clean Architecture");
        }

        @Test
        @DisplayName("should trim new title")
        void shouldTrimNewTitle() {
            CatalogEntry entry = CatalogEntry.create(ISBN, TITLE, AUTHOR);

            entry.updateTitle("  Clean Architecture  ");

            assertThat(entry.getTitle()).isEqualTo("Clean Architecture");
        }

        @Test
        @DisplayName("should throw when new title is null")
        void shouldThrowWhenNewTitleIsNull() {
            CatalogEntry entry = CatalogEntry.create(ISBN, TITLE, AUTHOR);

            assertThatThrownBy(() -> entry.updateTitle(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("Title cannot be null");
        }

        @Test
        @DisplayName("should throw when new title is blank")
        void shouldThrowWhenNewTitleIsBlank() {
            CatalogEntry entry = CatalogEntry.create(ISBN, TITLE, AUTHOR);

            assertThatThrownBy(() -> entry.updateTitle("   "))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Title cannot be blank");
        }

        @Test
        @DisplayName("should return self for fluent API")
        void shouldReturnSelfForFluentApi() {
            CatalogEntry entry = CatalogEntry.create(ISBN, TITLE, AUTHOR);

            CatalogEntry result = entry.updateTitle("Clean Architecture");

            assertThat(result).isSameAs(entry);
        }
    }

    @Nested
    @DisplayName("updateAuthor")
    class UpdateAuthor {

        @Test
        @DisplayName("should update author")
        void shouldUpdateAuthor() {
            CatalogEntry entry = CatalogEntry.create(ISBN, TITLE, AUTHOR);

            entry.updateAuthor("Uncle Bob");

            assertThat(entry.getAuthor()).isEqualTo("Uncle Bob");
        }

        @Test
        @DisplayName("should trim new author")
        void shouldTrimNewAuthor() {
            CatalogEntry entry = CatalogEntry.create(ISBN, TITLE, AUTHOR);

            entry.updateAuthor("  Uncle Bob  ");

            assertThat(entry.getAuthor()).isEqualTo("Uncle Bob");
        }

        @Test
        @DisplayName("should throw when new author is null")
        void shouldThrowWhenNewAuthorIsNull() {
            CatalogEntry entry = CatalogEntry.create(ISBN, TITLE, AUTHOR);

            assertThatThrownBy(() -> entry.updateAuthor(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("Author cannot be null");
        }

        @Test
        @DisplayName("should throw when new author is blank")
        void shouldThrowWhenNewAuthorIsBlank() {
            CatalogEntry entry = CatalogEntry.create(ISBN, TITLE, AUTHOR);

            assertThatThrownBy(() -> entry.updateAuthor("   "))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Author cannot be blank");
        }

        @Test
        @DisplayName("should return self for fluent API")
        void shouldReturnSelfForFluentApi() {
            CatalogEntry entry = CatalogEntry.create(ISBN, TITLE, AUTHOR);

            CatalogEntry result = entry.updateAuthor("Uncle Bob");

            assertThat(result).isSameAs(entry);
        }
    }
}
