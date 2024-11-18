package by.innowise.orderservice.repositories;

import by.innowise.orderservice.domain.Order;
import by.innowise.orderservice.domain.OrderStatus;
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
@ContextConfiguration(initializers = {OrderRepositoryTest.Initializer.class})
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class OrderRepositoryTest {

    @Container
    @SuppressWarnings("all")
    private static final PostgreSQLContainer<?> dbContainer = new PostgreSQLContainer<>("postgres:13.3").
            withDatabaseName("OrderServiceDB").
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
    private OrderRepository orderRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void createOrder() {
        Order order = Order.builder().
                userId(UUID.randomUUID()).
                status(OrderStatus.CREATED).
                price(BigDecimal.valueOf(100.34)).
                build();
        Order persistedOrder = orderRepository.save(order);
        Assertions.assertEquals(persistedOrder, entityManager.find(Order.class, persistedOrder.getId()));
    }

    @Test
    public void findOrderById() {
        Order order = Order.builder().
                userId(UUID.randomUUID()).
                status(OrderStatus.CREATED).
                price(BigDecimal.valueOf(100.34)).
                build();
        Order persistedOrder = entityManager.persist(order);
        Assertions.assertEquals(Optional.of(persistedOrder), orderRepository.findById(persistedOrder.getId()));
    }

    @Test
    public void updateOrder() {
        Order order = Order.builder().
                userId(UUID.randomUUID()).
                status(OrderStatus.CREATED).
                price(BigDecimal.valueOf(100.34)).
                build();
        Order persistedOrder = entityManager.persist(order);
        persistedOrder.setStatus(OrderStatus.CANCELED);
        orderRepository.save(persistedOrder);
        Assertions.assertEquals(persistedOrder, entityManager.find(Order.class, persistedOrder.getId()));
    }

    @Test
    public void deleteOrderById() {
        Order order = Order.builder().
                userId(UUID.randomUUID()).
                status(OrderStatus.CREATED).
                price(BigDecimal.valueOf(100.34)).
                build();
        Order persistedOrder = entityManager.persist(order);
        orderRepository.deleteById(persistedOrder.getId());
        Assertions.assertNull(entityManager.find(Order.class, persistedOrder.getId()));
    }
}
