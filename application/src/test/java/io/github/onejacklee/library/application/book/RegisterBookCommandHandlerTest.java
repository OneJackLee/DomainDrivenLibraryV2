package io.github.onejacklee.library.application.book;

import io.github.onejacklee.library.application.exception.CatalogEntryConflictException;
import io.github.onejacklee.library.common.application.IdGenerator;
import io.github.onejacklee.library.domain.book.Book;
import io.github.onejacklee.library.domain.book.BookRepository;
import io.github.onejacklee.library.domain.catalogentry.CatalogEntry;
import io.github.onejacklee.library.domain.catalogentry.CatalogEntryRepository;
import io.github.onejacklee.library.domain.catalogentry.Isbn;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RegisterBookCommandHandler")
class RegisterBookCommandHandlerTest {

    private static final String BOOK_ID = "01ARZ3NDEKTSV4RRFFQ69G5FAV";
    private static final String ISBN = "9780132350884";
    private static final String TITLE = "Clean Code";
    private static final String AUTHOR = "Robert C. Martin";

    @Mock
    private BookRepository bookRepository;

    @Mock
    private CatalogEntryRepository catalogEntryRepository;

    @Mock
    private IdGenerator idGenerator;

    private RegisterBookCommandHandler handler;

    @BeforeEach
    void setUp() {
        handler = new RegisterBookCommandHandler(bookRepository, catalogEntryRepository, idGenerator);
    }

    @Nested
    @DisplayName("when catalog entry does not exist")
    class WhenCatalogEntryDoesNotExist {

        @BeforeEach
        void setUp() {
            when(idGenerator.generate()).thenReturn(BOOK_ID);
            when(catalogEntryRepository.findByIsbn(any(Isbn.class))).thenReturn(Optional.empty());
        }

        @Test
        @DisplayName("should create new catalog entry and book")
        void shouldCreateNewCatalogEntryAndBook() {
            var command = new RegisterBookCommand(ISBN, TITLE, AUTHOR);

            BookDetailsDto result = handler.handle(command);

            assertThat(result.id()).isEqualTo(BOOK_ID);
            assertThat(result.isbn()).isEqualTo(ISBN);
            assertThat(result.title()).isEqualTo(TITLE);
            assertThat(result.author()).isEqualTo(AUTHOR);
            assertThat(result.available()).isTrue();
            assertThat(result.borrowerId()).isNull();
            assertThat(result.borrowedOn()).isNull();
        }

        @Test
        @DisplayName("should save catalog entry")
        void shouldSaveCatalogEntry() {
            var command = new RegisterBookCommand(ISBN, TITLE, AUTHOR);

            handler.handle(command);

            ArgumentCaptor<CatalogEntry> captor = ArgumentCaptor.forClass(CatalogEntry.class);
            verify(catalogEntryRepository).save(captor.capture());
            CatalogEntry saved = captor.getValue();
            assertThat(saved.getIsbn().value()).isEqualTo(ISBN);
            assertThat(saved.getTitle()).isEqualTo(TITLE);
            assertThat(saved.getAuthor()).isEqualTo(AUTHOR);
        }

        @Test
        @DisplayName("should save book")
        void shouldSaveBook() {
            var command = new RegisterBookCommand(ISBN, TITLE, AUTHOR);

            handler.handle(command);

            ArgumentCaptor<Book> captor = ArgumentCaptor.forClass(Book.class);
            verify(bookRepository).save(captor.capture());
            Book saved = captor.getValue();
            assertThat(saved.getId().value()).isEqualTo(BOOK_ID);
            assertThat(saved.getIsbn().value()).isEqualTo(ISBN);
            assertThat(saved.isAvailable()).isTrue();
        }
    }

    @Nested
    @DisplayName("when catalog entry exists with matching metadata")
    class WhenCatalogEntryExistsWithMatchingMetadata {

        private CatalogEntry existingEntry;

        @BeforeEach
        void setUp() {
            when(idGenerator.generate()).thenReturn(BOOK_ID);
            existingEntry = CatalogEntry.create(ISBN, TITLE, AUTHOR);
            when(catalogEntryRepository.findByIsbn(any(Isbn.class))).thenReturn(Optional.of(existingEntry));
        }

        @Test
        @DisplayName("should not create new catalog entry")
        void shouldNotCreateNewCatalogEntry() {
            var command = new RegisterBookCommand(ISBN, TITLE, AUTHOR);

            handler.handle(command);

            verify(catalogEntryRepository, never()).save(any());
        }

        @Test
        @DisplayName("should create book with existing catalog entry")
        void shouldCreateBookWithExistingCatalogEntry() {
            var command = new RegisterBookCommand(ISBN, TITLE, AUTHOR);

            BookDetailsDto result = handler.handle(command);

            assertThat(result.isbn()).isEqualTo(ISBN);
            assertThat(result.title()).isEqualTo(TITLE);
            verify(bookRepository).save(any(Book.class));
        }

        @Test
        @DisplayName("should match case-insensitively")
        void shouldMatchCaseInsensitively() {
            var command = new RegisterBookCommand(ISBN, "CLEAN CODE", "ROBERT C. MARTIN");

            BookDetailsDto result = handler.handle(command);

            assertThat(result.isbn()).isEqualTo(ISBN);
            verify(catalogEntryRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("when catalog entry exists with different metadata")
    class WhenCatalogEntryExistsWithDifferentMetadata {

        @BeforeEach
        void setUp() {
            CatalogEntry existingEntry = CatalogEntry.create(ISBN, TITLE, AUTHOR);
            when(catalogEntryRepository.findByIsbn(any(Isbn.class))).thenReturn(Optional.of(existingEntry));
        }

        @Test
        @DisplayName("should throw conflict exception for different title")
        void shouldThrowConflictExceptionForDifferentTitle() {
            var command = new RegisterBookCommand(ISBN, "Different Title", AUTHOR);

            assertThatThrownBy(() -> handler.handle(command))
                    .isInstanceOf(CatalogEntryConflictException.class);
        }

        @Test
        @DisplayName("should throw conflict exception for different author")
        void shouldThrowConflictExceptionForDifferentAuthor() {
            var command = new RegisterBookCommand(ISBN, TITLE, "Different Author");

            assertThatThrownBy(() -> handler.handle(command))
                    .isInstanceOf(CatalogEntryConflictException.class);
        }

        @Test
        @DisplayName("should not save book when conflict occurs")
        void shouldNotSaveBookWhenConflictOccurs() {
            var command = new RegisterBookCommand(ISBN, "Different Title", AUTHOR);

            try {
                handler.handle(command);
            } catch (CatalogEntryConflictException ignored) {
            }

            verify(bookRepository, never()).save(any());
        }
    }
}
