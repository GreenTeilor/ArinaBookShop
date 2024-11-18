package by.innowise.productservice.services;

import by.innowise.productservice.dto.BookDto;
import by.innowise.productservice.dto.validationgroups.CreateProduct;
import by.innowise.productservice.dto.validationgroups.UpdateProduct;
import by.innowise.productservice.exceptions.ProductExistsException;
import by.innowise.productservice.exceptions.NoProductExistsException;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookService {

    @Validated(CreateProduct.class)
    BookDto create(@Valid BookDto bookDto) throws ProductExistsException;

    Optional<BookDto> read(UUID id);

    List<BookDto> readAll();

    @Validated(UpdateProduct.class)
    BookDto update(@Valid BookDto bookDto) throws NoProductExistsException;

    UUID delete(UUID id) throws NoProductExistsException;
}
