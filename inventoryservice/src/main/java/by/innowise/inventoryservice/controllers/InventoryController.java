package by.innowise.inventoryservice.controllers;

import by.innowise.inventoryservice.dto.PositionDto;
import by.innowise.inventoryservice.dto.UpdateProductAmountResponseDto;
import by.innowise.inventoryservice.exceptions.NoProductWithIdExistsException;
import by.innowise.inventoryservice.exceptions.NotEnoughProductInInventoryException;
import by.innowise.inventoryservice.services.InventoryService;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public Optional<PositionDto> readPosition(@PathVariable UUID id) {
        return inventoryService.readPosition(id);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<PositionDto> readAllPositions() {
        return inventoryService.readAllPositions();
    }

    @PutMapping("/add")
    @PreAuthorize("isAuthenticated()")
    @RolesAllowed("ADMIN")
    public UpdateProductAmountResponseDto addPositionAmount(@RequestBody PositionDto positionDto)
            throws NoProductWithIdExistsException {
        return inventoryService.addPositionAmount(positionDto);
    }

    @PutMapping("/sub")
    @PreAuthorize("isAuthenticated()")
    public UpdateProductAmountResponseDto subPositionAmount(@RequestBody PositionDto positionDto)
            throws NoProductWithIdExistsException, NotEnoughProductInInventoryException {
        return inventoryService.subPositionAmount(positionDto);
    }
}
