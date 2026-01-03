package io.github.onejacklee.library.application.borrower;

import io.github.onejacklee.library.application.exception.BorrowerEmailAlreadyExistsException;
import io.github.onejacklee.library.common.application.IdGenerator;
import io.github.onejacklee.library.domain.borrower.Borrower;
import io.github.onejacklee.library.domain.borrower.BorrowerRepository;
import io.github.onejacklee.library.domain.borrower.EmailAddress;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RegisterBorrowerCommandHandler")
class RegisterBorrowerCommandHandlerTest {

    private static final String BORROWER_ID = "01ARZ3NDEKTSV4RRFFQ69G5FAV";
    private static final String NAME = "John Doe";
    private static final String EMAIL = "john.doe@example.com";

    @Mock
    private BorrowerRepository borrowerRepository;

    @Mock
    private IdGenerator idGenerator;

    private RegisterBorrowerCommandHandler handler;

    @BeforeEach
    void setUp() {
        handler = new RegisterBorrowerCommandHandler(borrowerRepository, idGenerator);
    }

    @Nested
    @DisplayName("when email does not exist")
    class WhenEmailDoesNotExist {

        @BeforeEach
        void setUp() {
            when(idGenerator.generate()).thenReturn(BORROWER_ID);
            when(borrowerRepository.existsByEmailAddress(any(EmailAddress.class))).thenReturn(false);
        }

        @Test
        @DisplayName("should create new borrower")
        void shouldCreateNewBorrower() {
            var command = new RegisterBorrowerCommand(NAME, EMAIL);

            BorrowerDto result = handler.handle(command);

            assertThat(result.id()).isEqualTo(BORROWER_ID);
            assertThat(result.name()).isEqualTo(NAME);
            assertThat(result.emailAddress()).isEqualTo(EMAIL.toLowerCase());
        }

        @Test
        @DisplayName("should save borrower")
        void shouldSaveBorrower() {
            var command = new RegisterBorrowerCommand(NAME, EMAIL);

            handler.handle(command);

            ArgumentCaptor<Borrower> captor = ArgumentCaptor.forClass(Borrower.class);
            verify(borrowerRepository).save(captor.capture());
            Borrower saved = captor.getValue();
            assertThat(saved.getId().value()).isEqualTo(BORROWER_ID);
            assertThat(saved.getName()).isEqualTo(NAME);
            assertThat(saved.getEmailAddress().value()).isEqualTo(EMAIL.toLowerCase());
        }

        @Test
        @DisplayName("should normalize email to lowercase")
        void shouldNormalizeEmailToLowercase() {
            var command = new RegisterBorrowerCommand(NAME, "John.Doe@Example.COM");

            BorrowerDto result = handler.handle(command);

            assertThat(result.emailAddress()).isEqualTo("john.doe@example.com");
        }
    }

    @Nested
    @DisplayName("when email already exists")
    class WhenEmailAlreadyExists {

        @BeforeEach
        void setUp() {
            when(borrowerRepository.existsByEmailAddress(any(EmailAddress.class))).thenReturn(true);
        }

        @Test
        @DisplayName("should throw BorrowerEmailAlreadyExistsException")
        void shouldThrowBorrowerEmailAlreadyExistsException() {
            var command = new RegisterBorrowerCommand(NAME, EMAIL);

            assertThatThrownBy(() -> handler.handle(command))
                    .isInstanceOf(BorrowerEmailAlreadyExistsException.class);
        }

        @Test
        @DisplayName("should not save borrower")
        void shouldNotSaveBorrower() {
            var command = new RegisterBorrowerCommand(NAME, EMAIL);

            try {
                handler.handle(command);
            } catch (BorrowerEmailAlreadyExistsException ignored) {
            }

            verify(borrowerRepository, never()).save(any());
        }

        @Test
        @DisplayName("should not generate ID")
        void shouldNotGenerateId() {
            var command = new RegisterBorrowerCommand(NAME, EMAIL);

            try {
                handler.handle(command);
            } catch (BorrowerEmailAlreadyExistsException ignored) {
            }

            verify(idGenerator, never()).generate();
        }
    }

    @Nested
    @DisplayName("validation")
    class Validation {

        @Test
        @DisplayName("should throw for invalid email format")
        void shouldThrowForInvalidEmailFormat() {
            var command = new RegisterBorrowerCommand(NAME, "invalid-email");

            assertThatThrownBy(() -> handler.handle(command))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Invalid email address");
        }
    }
}
