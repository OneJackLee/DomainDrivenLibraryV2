package io.github.onejacklee.library.application.book;

public record RegisterBookCommand(
        String isbn,
        String title,
        String author
) {
}
