package io.github.onejacklee.library.presentation.controller;

import io.github.onejacklee.library.application.catalogentry.GetCatalogEntryByIsbnQuery;
import io.github.onejacklee.library.application.catalogentry.GetCatalogEntryByIsbnQueryHandler;
import io.github.onejacklee.library.application.catalogentry.UpdateCatalogEntryCommand;
import io.github.onejacklee.library.application.catalogentry.UpdateCatalogEntryCommandHandler;
import io.github.onejacklee.library.presentation.dto.request.UpdateCatalogEntryRequest;
import io.github.onejacklee.library.presentation.dto.response.CatalogEntryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/catalog-entries")
@RequiredArgsConstructor
@Tag(name = "Catalog Entries", description = "Catalog entry management endpoints")
public class CatalogEntriesController {

    private final GetCatalogEntryByIsbnQueryHandler getByIsbnHandler;
    private final UpdateCatalogEntryCommandHandler updateHandler;

    @GetMapping("/{isbn}")
    @Operation(summary = "Get catalog entry by ISBN")
    public CatalogEntryResponse getByIsbn(@PathVariable String isbn) {
        var query = new GetCatalogEntryByIsbnQuery(isbn);
        return CatalogEntryResponse.from(getByIsbnHandler.handle(query));
    }

    @PutMapping("/{isbn}")
    @Operation(summary = "Update catalog entry")
    public CatalogEntryResponse update(@PathVariable String isbn,
                                       @Valid @RequestBody UpdateCatalogEntryRequest request) {
        var command = new UpdateCatalogEntryCommand(isbn, request.title(), request.author());
        return CatalogEntryResponse.from(updateHandler.handle(command));
    }
}
