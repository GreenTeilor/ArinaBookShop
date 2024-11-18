package by.innowise.inventoryservice.repositories;

import by.innowise.inventoryservice.domain.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface InventoryRepository extends JpaRepository<Position, UUID> {
}
