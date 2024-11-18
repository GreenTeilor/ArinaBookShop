package by.innowise.orderservice.repositories;

import by.innowise.orderservice.domain.OrderProductId;
import by.innowise.orderservice.domain.OrdersProducts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrdersProductsRepository extends JpaRepository<OrdersProducts, OrderProductId> {
    List<OrdersProducts> findAllByIdOrderId(UUID id);
    void deleteAllByIdOrderId(UUID id);
}
