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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UpdateCatalogEntryCommandHandler")
class UpdateCatalogEntryCommandHandlerTest {

    private static final String ISBN = "9780132350884";
    private static final String TITLE = "Clean Code";
    private static final String AUTHOR = "Robert C. Martin";
    private static final String NEW_TITLE = "Clean Code: A Handbook";
    private static final String NEW_AUTHOR = "Robert Cecil Martin";

    @Mock
    private CatalogEntryRepository catalogEntryRepository;

    private UpdateCatalogEntryCommandHandler handler;

    @BeforeEach
    void setUp() {
        handler = new UpdateCatalogEntryCommandHandler(catalogEntryRepository);
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
        @DisplayName("should update title and author")
        void shouldUpdateTitleAndAuthor() {
            var command = new UpdateCatalogEntryCommand(ISBN, NEW_TITLE, NEW_AUTHOR);

            CatalogEntryDto result = handler.handle(command);

            assertThat(result.isbn()).isEqualTo(ISBN);
            assertThat(result.title()).isEqualTo(NEW_TITLE);
            assertThat(result.author()).isEqualTo(NEW_AUTHOR);
        }

        @Test
        @DisplayName("should save updated catalog entry")
        void shouldSaveUpdatedCatalogEntry() {
            var command = new UpdateCatalogEntryCommand(ISBN, NEW_TITLE, NEW_AUTHOR);

            handler.handle(command);

            verify(catalogEntryRepository).save(catalogEntry);
            assertThat(catalogEntry.getTitle()).isEqualTo(NEW_TITLE);
            assertThat(catalogEntry.getAuthor()).isEqualTo(NEW_AUTHOR);
        }

        @Test
        @DisplayName("should normalize ISBN before lookup")
        void shouldNormalizeIsbnBeforeLookup() {
            var command = new UpdateCatalogEntryCommand("978-0-13-235088-4", NEW_TITLE, NEW_AUTHOR);

            CatalogEntryDto result = handler.handle(command);

            assertThat(result.isbn()).isEqualTo(ISBN);
        }

        @Test
        @DisplayName("should trim title and author")
        void shouldTrimTitleAndAuthor() {
            var command = new UpdateCatalogEntryCommand(ISBN, "  " + NEW_TITLE + "  ", "  " + NEW_AUTHOR + "  ");

            CatalogEntryDto result = handler.handle(command);

            assertThat(result.title()).isEqualTo(NEW_TITLE);
            assertThat(result.author()).isEqualTo(NEW_AUTHOR);
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
            var command = new UpdateCatalogEntryCommand(ISBN, NEW_TITLE, NEW_AUTHOR);

            assertThatThrownBy(() -> handler.handle(command))
                    .isInstanceOf(CatalogEntryNotFoundException.class);
        }

        @Test
        @DisplayName("should not save anything")
        void shouldNotSaveAnything() {
            var command = new UpdateCatalogEntryCommand(ISBN, NEW_TITLE, NEW_AUTHOR);

            try {
                handler.handle(command);
            } catch (CatalogEntryNotFoundException ignored) {
            }

            verify(catalogEntryRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("validation")
    class Validation {

        @Test
        @DisplayName("should throw for blank title")
        void shouldThrowForBlankTitle() {
            CatalogEntry catalogEntry = CatalogEntry.create(ISBN, TITLE, AUTHOR);
            when(catalogEntryRepository.findByIsbn(any(Isbn.class))).thenReturn(Optional.of(catalogEntry));

            var command = new UpdateCatalogEntryCommand(ISBN, "   ", NEW_AUTHOR);

            assertThatThrownBy(() -> handler.handle(command))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Title cannot be blank");
        }

        @Test
        @DisplayName("should throw for blank author")
        void shouldThrowForBlankAuthor() {
            CatalogEntry catalogEntry = CatalogEntry.create(ISBN, TITLE, AUTHOR);
            when(catalogEntryRepository.findByIsbn(any(Isbn.class))).thenReturn(Optional.of(catalogEntry));

            var command = new UpdateCatalogEntryCommand(ISBN, NEW_TITLE, "   ");

            assertThatThrownBy(() -> handler.handle(command))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Author cannot be blank");
        }

        @Test
        @DisplayName("should throw for invalid ISBN format")
        void shouldThrowForInvalidIsbnFormat() {
            var command = new UpdateCatalogEntryCommand("invalid", NEW_TITLE, NEW_AUTHOR);

            assertThatThrownBy(() -> handler.handle(command))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("ISBN must be 10 or 13 digits");
        }
    }
}
