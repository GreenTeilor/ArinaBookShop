package by.innowise.inventoryservice.services;

import by.innowise.inventoryservice.dto.PositionDto;
import by.innowise.inventoryservice.dto.UpdateProductAmountResponseDto;
import by.innowise.inventoryservice.exceptions.NoProductWithIdExistsException;
import by.innowise.inventoryservice.exceptions.NotEnoughProductInInventoryException;
import by.innowise.inventoryservice.exceptions.ProductWithIdExistsException;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InventoryService {
    void addPosition(UUID productId) throws ProductWithIdExistsException;
    void deletePosition(UUID productId) throws NoProductWithIdExistsException;
    Optional<PositionDto> readPosition(UUID productId);
    List<PositionDto> readAllPositions();
    UpdateProductAmountResponseDto addPositionAmount(@Valid PositionDto positionDto) throws NoProductWithIdExistsException;
    UpdateProductAmountResponseDto subPositionAmount(@Valid PositionDto positionDto)
            throws NoProductWithIdExistsException, NotEnoughProductInInventoryException;
}
