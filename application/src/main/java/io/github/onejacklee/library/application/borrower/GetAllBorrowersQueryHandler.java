package io.github.onejacklee.library.application.borrower;

import io.github.onejacklee.library.domain.borrower.BorrowerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetAllBorrowersQueryHandler {

    private final BorrowerRepository borrowerRepository;

    @Transactional(readOnly = true)
    public List<BorrowerDto> handle(GetAllBorrowersQuery query) {
        return borrowerRepository.findAll().stream()
                .map(BorrowerDto::from)
                .toList();
    }
}
