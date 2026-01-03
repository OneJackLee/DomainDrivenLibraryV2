package io.github.onejacklee.library.application.book;

import io.github.onejacklee.library.domain.book.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetAllBooksQueryHandler {

    private final BookRepository bookRepository;

    @Transactional(readOnly = true)
    public List<BookDetailsDto> handle(GetAllBooksQuery query) {
        return bookRepository.findAllWithCatalog().stream()
                .map(BookDetailsDto::from)
                .toList();
    }
}
