package by.innowise.userservice.repositories;

import by.innowise.userservice.domain.User;
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
import java.util.UUID;

@Testcontainers
@ContextConfiguration(initializers = {UserRepositoryTest.Initializer.class})
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {

    @Container
    @SuppressWarnings("all")
    private static final PostgreSQLContainer<?> dbContainer = new PostgreSQLContainer<>("postgres:13.3").
            withDatabaseName("UserServiceDB").
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
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void create() {
        User user = User.builder().
                id(UUID.randomUUID()).
                email("test@gmail.com").
                balance(BigDecimal.valueOf(0.00)).
                build();
        userRepository.save(user);
        Assertions.assertEquals(user, entityManager.find(User.class, user.getId()));
    }

    @Test
    public void findById() {
        User user = User.builder().
                id(UUID.randomUUID()).
                email("test@gmail.com").
                balance(BigDecimal.valueOf(0.00)).
                build();
        entityManager.persist(user);
        Assertions.assertEquals(Optional.of(user), userRepository.findById(user.getId()));
    }

    @Test
    public void findByEmail() {
        User user = User.builder().
                id(UUID.randomUUID()).
                email("test@gmail.com").
                balance(BigDecimal.valueOf(0.00)).
                build();
        entityManager.persist(user);
        Assertions.assertEquals(Optional.of(user), userRepository.findByEmail(user.getEmail()));
    }

    @Test
    public void update() {
        User user = User.builder().
                id(UUID.randomUUID()).
                email("test@gmail.com").
                balance(BigDecimal.valueOf(0.00)).
                build();
        entityManager.persist(user);
        user.setEmail("newTest@gmail.com");
        userRepository.save(user);
        Assertions.assertEquals(user, entityManager.find(User.class, user.getId()));
    }

    @Test
    public void delete() {
        User user = User.builder().
                id(UUID.randomUUID()).
                email("test@gmail.com").
                balance(BigDecimal.valueOf(0.00)).
                build();
        entityManager.persist(user);
        userRepository.delete(user);
        Assertions.assertNull(entityManager.find(User.class, user.getId()));
    }
}
