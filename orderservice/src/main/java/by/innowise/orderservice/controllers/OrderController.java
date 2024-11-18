package by.innowise.orderservice.controllers;

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
import by.innowise.orderservice.filters.ContextHolder;
import by.innowise.orderservice.services.OrderService;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public OrderResponseDto create(@RequestBody OrderRequestDto orderDto)
            throws NoProductWithIdExistsException, NotEnoughProductsInInventoryException,
            InsufficientFundsException, NoProductsInOrderException {
        return orderService.create(ContextHolder.getContext().getUserId(), orderDto);
    }

    @GetMapping("/{id}")
    public Optional<OrderResponseDto> read(@PathVariable UUID id) throws UserNotOrderOwnerException {
        return orderService.read(id);
    }

    @DeleteMapping("/{id}")
    @RolesAllowed("ADMIN")
    public UUID delete(@PathVariable UUID id) throws NoOrderWithIdExistsException {
        return orderService.delete(id);
    }

    @PutMapping("/updateStatus/{id}")
    @RolesAllowed({"ADMIN", "COURIER"})
    public UpdateOrderStatusResponseDto updateStatus(@PathVariable UUID id, @RequestParam OrderStatusDto status)
            throws NoOrderWithIdExistsException {
        return orderService.updateStatus(id, status);
    }
}
