package by.innowise.orderservice.repositories;

import by.innowise.orderservice.domain.OrderProductId;
import by.innowise.orderservice.domain.OrdersProducts;
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

import java.util.List;
import java.util.UUID;

@Testcontainers
@ContextConfiguration(initializers = {OrdersProductsRepositoryTest.Initializer.class})
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class OrdersProductsRepositoryTest {

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
    OrdersProductsRepository ordersProductsRepository;

    @Autowired
    TestEntityManager entityManager;

    @Test
    public void createOrderProduct() {
        OrdersProducts ordersProducts = OrdersProducts.builder().
                id(new OrderProductId(UUID.randomUUID(), UUID.randomUUID())).
                amount(3).
                build();
        OrdersProducts persistedOrderProduct = ordersProductsRepository.save(ordersProducts);
        Assertions.assertEquals(persistedOrderProduct, entityManager.find(OrdersProducts.class,
                persistedOrderProduct.getId()));
    }

    @Test
    public void findAllByOrderId() {
        UUID orderId1 = UUID.randomUUID();
        UUID orderId2 = UUID.randomUUID();
        OrdersProducts ordersProducts1 = OrdersProducts.builder().
                id(new OrderProductId(orderId1, UUID.randomUUID())).
                amount(3).
                build();
        OrdersProducts ordersProducts2 = OrdersProducts.builder().
                id(new OrderProductId(orderId1, UUID.randomUUID())).
                amount(4).
                build();
        OrdersProducts ordersProducts3 = OrdersProducts.builder().
                id(new OrderProductId(orderId2, UUID.randomUUID())).
                amount(5).
                build();
        OrdersProducts persisted1 = entityManager.persist(ordersProducts1);
        OrdersProducts persisted2 = entityManager.persist(ordersProducts2);
        entityManager.persist(ordersProducts3);
        Assertions.assertTrue(ordersProductsRepository.
                findAllByIdOrderId(orderId1).containsAll(List.of(persisted1, persisted2)));
    }

    @Test
    public void deleteAllByOrderId() {
        UUID orderId1 = UUID.randomUUID();
        UUID orderId2 = UUID.randomUUID();
        OrdersProducts ordersProducts1 = OrdersProducts.builder().
                id(new OrderProductId(orderId1, UUID.randomUUID())).
                amount(3).
                build();
        OrdersProducts ordersProducts2 = OrdersProducts.builder().
                id(new OrderProductId(orderId1, UUID.randomUUID())).
                amount(4).
                build();
        OrdersProducts ordersProducts3 = OrdersProducts.builder().
                id(new OrderProductId(orderId2, UUID.randomUUID())).
                amount(5).
                build();
        entityManager.persist(ordersProducts1);
        entityManager.persist(ordersProducts2);
        OrdersProducts persisted3 = entityManager.persist(ordersProducts3);
        ordersProductsRepository.deleteAllByIdOrderId(orderId1);
        Assertions.assertTrue(ordersProductsRepository.
                findAllByIdOrderId(orderId2).contains(persisted3));
    }
}
