package by.innowise.inventoryservice.repositories;

import by.innowise.inventoryservice.domain.Position;
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

import java.util.Optional;
import java.util.UUID;

@Testcontainers
@ContextConfiguration(initializers = {InventoryRepositoryTest.Initializer.class})
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class InventoryRepositoryTest {

    @Container
    @SuppressWarnings("all")
    private static final PostgreSQLContainer<?> dbContainer = new PostgreSQLContainer<>("postgres:13.3").
            withDatabaseName("InventoryServiceDB").
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
    private InventoryRepository inventoryRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void createPosition() {
        Position position = Position.builder().
                productId(UUID.randomUUID()).
                amount(3).
                build();
        Position persistedPosition = inventoryRepository.save(position);
        Assertions.assertEquals(persistedPosition, entityManager.find(Position.class,
                persistedPosition.getProductId()));
    }

    @Test
    public void findPositionById() {
        Position position = Position.builder().
                productId(UUID.randomUUID()).
                amount(3).
                build();
        Position persistedPosition = entityManager.persist(position);
        Assertions.assertEquals(Optional.of(persistedPosition), inventoryRepository.
                findById(persistedPosition.getProductId()));
    }

    @Test
    public void updatePosition() {
        Position position = Position.builder().
                productId(UUID.randomUUID()).
                amount(3).
                build();
        Position persistedPosition = entityManager.persist(position);
        persistedPosition.setAmount(4);
        inventoryRepository.save(persistedPosition);
        Assertions.assertEquals(persistedPosition, entityManager.
                find(Position.class, persistedPosition.getProductId()));
    }

    @Test
    public void deletePositionById() {
        Position position = Position.builder().
                productId(UUID.randomUUID()).
                amount(3).
                build();
        Position persistedPosition = entityManager.persist(position);
        inventoryRepository.deleteById(persistedPosition.getProductId());
        Assertions.assertNull(entityManager.find(Position.class, persistedPosition.getProductId()));
    }
}
