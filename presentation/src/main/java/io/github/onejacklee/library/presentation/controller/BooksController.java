package io.github.onejacklee.library.presentation.controller;

import io.github.onejacklee.library.application.book.*;
import io.github.onejacklee.library.presentation.dto.request.BorrowBookRequest;
import io.github.onejacklee.library.presentation.dto.request.RegisterBookRequest;
import io.github.onejacklee.library.presentation.dto.request.ReturnBookRequest;
import io.github.onejacklee.library.presentation.dto.response.BookResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Tag(name = "Books", description = "Book management endpoints")
public class BooksController {

    private final RegisterBookCommandHandler registerHandler;
    private final GetAllBooksQueryHandler getAllHandler;
    private final BorrowBookCommandHandler borrowHandler;
    private final ReturnBookCommandHandler returnHandler;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register a new book")
    public BookResponse register(@Valid @RequestBody RegisterBookRequest request) {
        var command = new RegisterBookCommand(request.isbn(), request.title(), request.author());
        return BookResponse.from(registerHandler.handle(command));
    }

    @GetMapping
    @Operation(summary = "Get all books")
    public List<BookResponse> getAll() {
        return getAllHandler.handle(new GetAllBooksQuery()).stream()
                .map(BookResponse::from)
                .toList();
    }

    @PostMapping("/{bookId}/borrow")
    @Operation(summary = "Borrow a book")
    public BookResponse borrow(@PathVariable String bookId,
                               @Valid @RequestBody BorrowBookRequest request) {
        var command = new BorrowBookCommand(bookId, request.borrowerId());
        return BookResponse.from(borrowHandler.handle(command));
    }

    @PostMapping("/{bookId}/return")
    @Operation(summary = "Return a book")
    public BookResponse returnBook(@PathVariable String bookId,
                                   @Valid @RequestBody ReturnBookRequest request) {
        var command = new ReturnBookCommand(bookId, request.borrowerId());
        return BookResponse.from(returnHandler.handle(command));
    }
}
