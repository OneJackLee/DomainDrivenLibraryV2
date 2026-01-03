package io.github.onejacklee.library.application.borrower;

import io.github.onejacklee.library.domain.borrower.Borrower;
import io.github.onejacklee.library.domain.borrower.BorrowerId;
import io.github.onejacklee.library.domain.borrower.BorrowerRepository;
import io.github.onejacklee.library.domain.borrower.EmailAddress;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetAllBorrowersQueryHandler")
class GetAllBorrowersQueryHandlerTest {

    @Mock
    private BorrowerRepository borrowerRepository;

    private GetAllBorrowersQueryHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GetAllBorrowersQueryHandler(borrowerRepository);
    }

    @Test
    @DisplayName("should return empty list when no borrowers exist")
    void shouldReturnEmptyListWhenNoBorrowersExist() {
        when(borrowerRepository.findAll()).thenReturn(List.of());

        List<BorrowerDto> result = handler.handle(new GetAllBorrowersQuery());

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("should return all borrowers")
    void shouldReturnAllBorrowers() {
        Borrower borrower1 = Borrower.register(
                BorrowerId.create("01ARZ3NDEKTSV4RRFFQ69G5FAV"),
                "John Doe",
                EmailAddress.create("john@example.com")
        );
        Borrower borrower2 = Borrower.register(
                BorrowerId.create("01ARZ3NDEKTSV4RRFFQ69G5FAW"),
                "Jane Doe",
                EmailAddress.create("jane@example.com")
        );
        when(borrowerRepository.findAll()).thenReturn(List.of(borrower1, borrower2));

        List<BorrowerDto> result = handler.handle(new GetAllBorrowersQuery());

        assertThat(result).hasSize(2);

        BorrowerDto dto1 = result.get(0);
        assertThat(dto1.id()).isEqualTo("01ARZ3NDEKTSV4RRFFQ69G5FAV");
        assertThat(dto1.name()).isEqualTo("John Doe");
        assertThat(dto1.emailAddress()).isEqualTo("john@example.com");

        BorrowerDto dto2 = result.get(1);
        assertThat(dto2.id()).isEqualTo("01ARZ3NDEKTSV4RRFFQ69G5FAW");
        assertThat(dto2.name()).isEqualTo("Jane Doe");
        assertThat(dto2.emailAddress()).isEqualTo("jane@example.com");
    }

    @Test
    @DisplayName("should map borrower fields correctly")
    void shouldMapBorrowerFieldsCorrectly() {
        Borrower borrower = Borrower.register(
                BorrowerId.create("01ARZ3NDEKTSV4RRFFQ69G5FAV"),
                "John Doe",
                EmailAddress.create("john@example.com")
        );
        when(borrowerRepository.findAll()).thenReturn(List.of(borrower));

        List<BorrowerDto> result = handler.handle(new GetAllBorrowersQuery());

        assertThat(result).hasSize(1);
        assertThat(result.get(0))
                .extracting(BorrowerDto::id, BorrowerDto::name, BorrowerDto::emailAddress)
                .containsExactly("01ARZ3NDEKTSV4RRFFQ69G5FAV", "John Doe", "john@example.com");
    }
}
