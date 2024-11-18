package by.innowise.productservice.repositories;

import by.innowise.productservice.domain.Book;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.Optional;

@Testcontainers
@ContextConfiguration(initializers = {BookRepositoryTest.Initializer.class})
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class BookRepositoryTest {

    @Container
    @SuppressWarnings("all")
    private static final PostgreSQLContainer<?> dbContainer = new PostgreSQLContainer<>("postgres:13.3").
            withDatabaseName("ProductServiceDB").
            withUsername("postgres").
            withPassword("postgres");

    protected static class Initializer
            implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    "spring.datasource.url=" + dbContainer.getJdbcUrl(),
                    "spring.datasource.username=" + dbContainer.getUsername(),
                    "spring.datasource.password=" + dbContainer.getPassword()
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void createBook() {
        Book book = Book.builder().
                title("title").
                genre("genre").
                author("author").
                publicationYear(1978).
                weight(BigDecimal.valueOf(0.5)).
                price(BigDecimal.valueOf(78.99)).
                build();
        Book persistedBook = bookRepository.save(book);
        Assertions.assertEquals(persistedBook, entityManager.find(Book.class, persistedBook.getId()));
    }

    @Test
    public void findBookById() {
        Book book = Book.builder().
                title("title").
                genre("genre").
                author("author").
                publicationYear(1978).
                weight(BigDecimal.valueOf(0.5)).
                price(BigDecimal.valueOf(78.99)).
                build();
        Book persistedBook = entityManager.persist(book);
        Assertions.assertEquals(Optional.of(persistedBook), bookRepository.findById(persistedBook.getId()));
    }

    @Test
    public void updateBook() {
        Book book = Book.builder().
                title("title").
                genre("genre").
                author("author").
                publicationYear(1978).
                weight(BigDecimal.valueOf(0.5)).
                price(BigDecimal.valueOf(78.99)).
                build();
        Book persistedBook = entityManager.persist(book);
        persistedBook.setTitle("new title");
        bookRepository.save(persistedBook);
        Assertions.assertEquals(persistedBook, entityManager.find(Book.class, persistedBook.getId()));
    }

    @Test
    public void deleteBookById() {
        Book book = Book.builder().
                title("title").
                genre("genre").
                author("author").
                publicationYear(1978).
                weight(BigDecimal.valueOf(0.5)).
                price(BigDecimal.valueOf(78.99)).
                build();
        Book persistedBook = entityManager.persist(book);
        bookRepository.deleteById(persistedBook.getId());
        Assertions.assertNull(entityManager.find(Book.class, persistedBook.getId()));
    }
}
