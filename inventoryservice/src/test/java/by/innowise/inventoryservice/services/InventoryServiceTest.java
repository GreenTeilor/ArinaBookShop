package by.innowise.inventoryservice.services;

import by.innowise.inventoryservice.domain.Position;
import by.innowise.inventoryservice.dto.PositionDto;
import by.innowise.inventoryservice.dto.UpdateProductAmountResponseDto;
import by.innowise.inventoryservice.exceptions.NoProductWithIdExistsException;
import by.innowise.inventoryservice.exceptions.NotEnoughProductInInventoryException;
import by.innowise.inventoryservice.repositories.InventoryRepository;
import by.innowise.inventoryservice.services.impl.InventoryServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class InventoryServiceTest {

    @Mock
    InventoryRepository inventoryRepository;

    @InjectMocks
    InventoryServiceImpl inventoryService;

    @Test
    public void addPositionAmountWhenProductDoesntExist() {
        Mockito.when(inventoryRepository.findById(any(UUID.class))).thenReturn(Optional.empty());
        Assertions.assertThrows(NoProductWithIdExistsException.class,
                () -> inventoryService.addPositionAmount(PositionDto.builder().
                        productId(UUID.randomUUID()).
                        amount(4).
                        build()));
    }

    @Test
    public void subPositionAmountWhenProductDoesntExist() {
        Mockito.when(inventoryRepository.findById(any(UUID.class))).thenReturn(Optional.empty());
        Assertions.assertThrows(NoProductWithIdExistsException.class,
                () -> inventoryService.subPositionAmount(PositionDto.builder().
                        productId(UUID.randomUUID()).
                        amount(4).
                        build()));
    }

    @Test
    public void subPositionAmountWhenNotEnoughProductsInInventory() {
        UUID productId = UUID.randomUUID();
        Mockito.when(inventoryRepository.findById(productId)).thenReturn(Optional.of(Position.builder().
                productId(productId).
                amount(4).
                build()));
        Assertions.assertThrows(NotEnoughProductInInventoryException.class,
                () -> inventoryService.subPositionAmount(PositionDto.builder().
                        productId(productId).
                        amount(5).
                        build()));
    }

    @Test
    public void addPositionAmountSuccessfully() throws NoProductWithIdExistsException {
        UUID productId = UUID.randomUUID();
        Mockito.when(inventoryRepository.findById(productId)).thenReturn(Optional.of(Position.builder().
                productId(productId).
                amount(4).
                build()));
        Assertions.assertEquals(UpdateProductAmountResponseDto.builder().
                updatedAmount(8).
                build(), inventoryService.addPositionAmount(PositionDto.builder().
                        productId(productId).
                        amount(4).
                        build()));
    }

    @Test
    public void subPositionAmountSuccessfully() throws
            NoProductWithIdExistsException, NotEnoughProductInInventoryException {
        UUID productId = UUID.randomUUID();
        Mockito.when(inventoryRepository.findById(productId)).thenReturn(Optional.of(Position.builder().
                productId(productId).
                amount(4).
                build()));
        Assertions.assertEquals(UpdateProductAmountResponseDto.builder().
                updatedAmount(0).
                build(), inventoryService.subPositionAmount(PositionDto.builder().
                productId(productId).
                amount(4).
                build()));
    }
}
