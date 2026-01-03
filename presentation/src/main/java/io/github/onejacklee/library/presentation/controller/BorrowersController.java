package io.github.onejacklee.library.presentation.controller;

import io.github.onejacklee.library.application.borrower.GetAllBorrowersQuery;
import io.github.onejacklee.library.application.borrower.GetAllBorrowersQueryHandler;
import io.github.onejacklee.library.application.borrower.RegisterBorrowerCommand;
import io.github.onejacklee.library.application.borrower.RegisterBorrowerCommandHandler;
import io.github.onejacklee.library.presentation.dto.request.RegisterBorrowerRequest;
import io.github.onejacklee.library.presentation.dto.response.BorrowerResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/borrowers")
@RequiredArgsConstructor
@Tag(name = "Borrowers", description = "Borrower management endpoints")
public class BorrowersController {

    private final RegisterBorrowerCommandHandler registerHandler;
    private final GetAllBorrowersQueryHandler getAllHandler;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register a new borrower")
    public BorrowerResponse register(@Valid @RequestBody RegisterBorrowerRequest request) {
        var command = new RegisterBorrowerCommand(request.name(), request.emailAddress());
        return BorrowerResponse.from(registerHandler.handle(command));
    }

    @GetMapping
    @Operation(summary = "Get all borrowers")
    public List<BorrowerResponse> getAll() {
        return getAllHandler.handle(new GetAllBorrowersQuery()).stream()
                .map(BorrowerResponse::from)
                .toList();
    }
}
