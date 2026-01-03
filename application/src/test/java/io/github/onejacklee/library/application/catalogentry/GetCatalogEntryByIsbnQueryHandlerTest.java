package io.github.onejacklee.library.application.catalogentry;

import io.github.onejacklee.library.application.exception.CatalogEntryNotFoundException;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetCatalogEntryByIsbnQueryHandler")
class GetCatalogEntryByIsbnQueryHandlerTest {

    private static final String ISBN = "9780132350884";
    private static final String TITLE = "Clean Code";
    private static final String AUTHOR = "Robert C. Martin";

    @Mock
    private CatalogEntryRepository catalogEntryRepository;

    private GetCatalogEntryByIsbnQueryHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GetCatalogEntryByIsbnQueryHandler(catalogEntryRepository);
    }

    @Nested
    @DisplayName("when catalog entry exists")
    class WhenCatalogEntryExists {

        private CatalogEntry catalogEntry;

        @BeforeEach
        void setUp() {
            catalogEntry = CatalogEntry.create(ISBN, TITLE, AUTHOR);
            when(catalogEntryRepository.findByIsbn(any(Isbn.class))).thenReturn(Optional.of(catalogEntry));
        }

        @Test
        @DisplayName("should return catalog entry")
        void shouldReturnCatalogEntry() {
            var query = new GetCatalogEntryByIsbnQuery(ISBN);

            CatalogEntryDto result = handler.handle(query);

            assertThat(result.isbn()).isEqualTo(ISBN);
            assertThat(result.title()).isEqualTo(TITLE);
            assertThat(result.author()).isEqualTo(AUTHOR);
        }

        @Test
        @DisplayName("should normalize ISBN before lookup")
        void shouldNormalizeIsbnBeforeLookup() {
            var query = new GetCatalogEntryByIsbnQuery("978-0-13-235088-4");

            CatalogEntryDto result = handler.handle(query);

            assertThat(result.isbn()).isEqualTo(ISBN);
        }
    }

    @Nested
    @DisplayName("when catalog entry does not exist")
    class WhenCatalogEntryDoesNotExist {

        @BeforeEach
        void setUp() {
            when(catalogEntryRepository.findByIsbn(any(Isbn.class))).thenReturn(Optional.empty());
        }

        @Test
        @DisplayName("should throw CatalogEntryNotFoundException")
        void shouldThrowCatalogEntryNotFoundException() {
            var query = new GetCatalogEntryByIsbnQuery(ISBN);

            assertThatThrownBy(() -> handler.handle(query))
                    .isInstanceOf(CatalogEntryNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("validation")
    class Validation {

        @Test
        @DisplayName("should throw for invalid ISBN format")
        void shouldThrowForInvalidIsbnFormat() {
            var query = new GetCatalogEntryByIsbnQuery("invalid");

            assertThatThrownBy(() -> handler.handle(query))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("ISBN must be 10 or 13 digits");
        }
    }
}
