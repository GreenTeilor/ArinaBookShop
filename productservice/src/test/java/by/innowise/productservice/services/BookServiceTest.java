package by.innowise.productservice.services;

import by.innowise.productservice.domain.Book;
import by.innowise.productservice.dto.BookDto;
import by.innowise.productservice.dto.InventoryChangeDto;
import by.innowise.productservice.dto.converters.BookConverter;
import by.innowise.productservice.exceptions.ProductExistsException;
import by.innowise.productservice.exceptions.NoProductExistsException;
import by.innowise.productservice.repositories.BookRepository;
import by.innowise.productservice.services.impl.BookServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookConverter bookConverter;

    @Mock
    @SuppressWarnings("unused")
    private KafkaTemplate<String, InventoryChangeDto> kafkaTemplate;

    @InjectMocks
    BookServiceImpl bookService;

    @Test
    public void createBookWithTitleThatDoesntExist()
            throws ProductExistsException, NoSuchFieldException, IllegalAccessException {
        UUID id = UUID.fromString("439cba0c-09f6-43cf-bdf0-873eab388b6c");
        BookDto bookDtoWithoutId = BookDto.builder().
                id(id).
                title("title").
                genre("genre").
                author("author").
                publicationYear(1978).
                weight(BigDecimal.valueOf(0.5)).
                price(BigDecimal.valueOf(78.99)).
                build();
        Book bookBeforeSave = Book.builder().
                title("title").
                genre("genre").
                author("author").
                publicationYear(1978).
                weight(BigDecimal.valueOf(0.5)).
                price(BigDecimal.valueOf(78.99)).
                build();
        Book bookAfterSave = Book.builder().
                id(id).
                title("title").
                genre("genre").
                author("author").
                publicationYear(1978).
                weight(BigDecimal.valueOf(0.5)).
                price(BigDecimal.valueOf(78.99)).
                build();
        Mockito.when(bookRepository.save(bookBeforeSave)).
                thenReturn(bookAfterSave);
        Mockito.when(bookConverter.toDto(bookAfterSave)).
                thenReturn(bookDtoWithoutId);
        Mockito.when(bookConverter.fromDto(bookDtoWithoutId)).
                thenReturn(bookBeforeSave);
        ReflectionTestUtils.setField(bookService, "bookService", bookService);
        Class<BookServiceImpl> clazz = BookServiceImpl.class;
        Field bookServiceField = clazz.getDeclaredField("bookService");
        bookServiceField.setAccessible(true);
        BookServiceImpl innerService = (BookServiceImpl)bookServiceField.get(bookService);
        Field inventoryChangeTopicField = clazz.getDeclaredField("inventoryChangeTopic");
        inventoryChangeTopicField.setAccessible(true);
        inventoryChangeTopicField.set(innerService, "some-topic");
        Assertions.assertEquals(bookService.create(bookDtoWithoutId), bookDtoWithoutId);
    }

    @Test
    public void createBookWithTitleThatExists() {
        UUID id = UUID.fromString("439cba0c-09f6-43cf-bdf0-873eab388b6c");
        BookDto bookDto = BookDto.builder().
                title("title").
                genre("genre").
                author("author").
                publicationYear(1978).
                weight(BigDecimal.valueOf(0.5)).
                price(BigDecimal.valueOf(78.99)).
                build();
        Book book = Book.builder().
                id(id).
                title("title").
                genre("genre").
                author("author").
                publicationYear(1978).
                weight(BigDecimal.valueOf(0.5)).
                price(BigDecimal.valueOf(78.99)).
                build();
        Mockito.when(bookRepository.findByTitle("title")).
                thenReturn(Optional.of(book));
        Assertions.assertThrows(ProductExistsException.class,
                () -> bookService.create(bookDto));
    }

    @Test
    public void readWhenBookExists() {
        UUID id = UUID.fromString("439cba0c-09f6-43cf-bdf0-873eab388b6c");
        BookDto bookDto = BookDto.builder().
                id(id).
                title("title").
                genre("genre").
                author("author").
                publicationYear(1978).
                weight(BigDecimal.valueOf(0.5)).
                price(BigDecimal.valueOf(78.99)).
                build();
        Book book = Book.builder().
                id(id).
                title("title").
                genre("genre").
                author("author").
                publicationYear(1978).
                weight(BigDecimal.valueOf(0.5)).
                price(BigDecimal.valueOf(78.99)).
                build();
        Mockito.when(bookRepository.findById(id)).
                thenReturn(Optional.of(book));
        Mockito.when(bookConverter.toDto(book)).
                thenReturn(bookDto);
        Assertions.assertEquals(bookService.read(id), Optional.of(bookDto));
    }

    @Test
    public void readWhenBookDoesntExist() {
        UUID id = UUID.fromString("439cba0c-09f6-43cf-bdf0-873eab388b6c");
        Mockito.when(bookRepository.findById(id)).
                thenReturn(Optional.empty());
        Assertions.assertEquals(bookService.read(id), Optional.empty());
    }

    @Test
    public void updateWhenBookDoesntExist() {
        UUID id = UUID.fromString("439cba0c-09f6-43cf-bdf0-873eab388b6c");
        BookDto bookDto = BookDto.builder().
                id(id).
                build();
        Mockito.when(bookRepository.findById(id)).
                thenReturn(Optional.empty());
        Assertions.assertThrows(NoProductExistsException.class,
                () -> bookService.update(bookDto));
    }

    @Test
    public void deleteWhenBookDoesntExist() {
        UUID id = UUID.fromString("439cba0c-09f6-43cf-bdf0-873eab388b6c");
        Mockito.when(bookRepository.findById(id)).
                thenReturn(Optional.empty());
        Assertions.assertThrows(NoProductExistsException.class,
                () -> bookService.delete(id));
    }
}
