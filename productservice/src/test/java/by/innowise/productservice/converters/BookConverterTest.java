package by.innowise.productservice.converters;

import by.innowise.productservice.domain.Book;
import by.innowise.productservice.dto.BookDto;
import by.innowise.productservice.dto.converters.BookConverter;
import by.innowise.productservice.dto.converters.BookConverterImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

public class BookConverterTest {

    private final BookConverter bookConverter = new BookConverterImpl();
    private static Book book;
    private static BookDto bookDto;

    @BeforeAll
    public static void setUp() {
        UUID id = UUID.fromString("439cba0c-09f6-43cf-bdf0-873eab388b6c");
        book = Book.builder().
                id(id).
                title("title").
                genre("genre").
                author("author").
                publicationYear(1978).
                weight(BigDecimal.valueOf(0.5)).
                price(BigDecimal.valueOf(78.99)).
                build();
        bookDto = BookDto.builder().
                id(id).
                title("title").
                genre("genre").
                author("author").
                publicationYear(1978).
                weight(BigDecimal.valueOf(0.5)).
                price(BigDecimal.valueOf(78.99)).
                build();

    }

    @Test
    public void bookToDto() {
        Assertions.assertEquals(bookDto, bookConverter.toDto(book));
    }

    @Test
    public void bookFromDto() {
        Assertions.assertEquals(book, bookConverter.fromDto(bookDto));
    }
}
