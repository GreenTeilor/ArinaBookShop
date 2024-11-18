package by.innowise.orderservice.services;

import by.innowise.orderservice.dto.products.BookDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;
import java.util.UUID;

@FeignClient("product-service")
@Service
public interface ProductService {

    @GetMapping(
            value = "/books/{id}",
            consumes = "application/json"
    )
    Optional<BookDto> read(@PathVariable UUID id);
}
