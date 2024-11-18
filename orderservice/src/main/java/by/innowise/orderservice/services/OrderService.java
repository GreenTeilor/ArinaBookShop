package by.innowise.orderservice.services;

import by.innowise.orderservice.dto.OrderRequestDto;
import by.innowise.orderservice.dto.OrderResponseDto;
import by.innowise.orderservice.dto.OrderStatusDto;
import by.innowise.orderservice.dto.UpdateOrderStatusResponseDto;
import by.innowise.orderservice.exceptions.InsufficientFundsException;
import by.innowise.orderservice.exceptions.NoOrderWithIdExistsException;
import by.innowise.orderservice.exceptions.NoProductWithIdExistsException;
import by.innowise.orderservice.exceptions.NoProductsInOrderException;
import by.innowise.orderservice.exceptions.NotEnoughProductsInInventoryException;
import by.innowise.orderservice.exceptions.UserNotOrderOwnerException;
import jakarta.validation.Valid;

import java.util.Optional;
import java.util.UUID;

public interface OrderService {
    OrderResponseDto create(UUID userId, @Valid OrderRequestDto orderDto)
            throws NoProductWithIdExistsException, NotEnoughProductsInInventoryException, InsufficientFundsException, NoProductsInOrderException;
    UUID delete(UUID id) throws NoOrderWithIdExistsException;
    Optional<OrderResponseDto> read(UUID id) throws UserNotOrderOwnerException;
    UpdateOrderStatusResponseDto updateStatus(UUID id, OrderStatusDto statusDto) throws NoOrderWithIdExistsException;
}
