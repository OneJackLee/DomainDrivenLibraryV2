package io.github.onejacklee.library.presentation.dto.response;

import io.github.onejacklee.library.application.book.BookDetailsDto;

import java.time.LocalDateTime;

public record BookResponse(
        String id,
        String isbn,
        String title,
        String author,
        boolean available,
        String borrowerId,
        LocalDateTime borrowedOn
) {
    public static BookResponse from(BookDetailsDto dto) {
        return new BookResponse(
                dto.id(),
                dto.isbn(),
                dto.title(),
                dto.author(),
                dto.available(),
                dto.borrowerId(),
                dto.borrowedOn()
        );
    }
}
