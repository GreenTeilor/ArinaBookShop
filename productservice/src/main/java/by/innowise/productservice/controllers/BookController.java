package by.innowise.productservice.controllers;

import by.innowise.productservice.dto.BookDto;
import by.innowise.productservice.exceptions.ProductExistsException;
import by.innowise.productservice.exceptions.NoProductExistsException;
import by.innowise.productservice.services.BookService;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @RolesAllowed("ADMIN")
    public BookDto create(@RequestBody BookDto bookDto) throws ProductExistsException {
        return bookService.create(bookDto);
    }

    @GetMapping("/{id}")
    public Optional<BookDto> read(@PathVariable UUID id) {
        return bookService.read(id);
    }

    @GetMapping
    public List<BookDto>  readAll() {
        return bookService.readAll();
    }

    @PutMapping
    @PreAuthorize("isAuthenticated()")
    @RolesAllowed("ADMIN")
    public BookDto update(@RequestBody BookDto bookDto) throws NoProductExistsException {
        return bookService.update(bookDto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @RolesAllowed("ADMIN")
    public UUID delete(@PathVariable UUID id) throws NoProductExistsException {
        return bookService.delete(id);
    }
}
