package by.innowise.inventoryservice.services.impl;

import by.innowise.inventoryservice.domain.Position;
import by.innowise.inventoryservice.dto.InventoryChangeDto;
import by.innowise.inventoryservice.dto.PositionDto;
import by.innowise.inventoryservice.dto.UpdateProductAmountResponseDto;
import by.innowise.inventoryservice.dto.converters.PositionConverter;
import by.innowise.inventoryservice.exceptions.Messages;
import by.innowise.inventoryservice.exceptions.NoProductWithIdExistsException;
import by.innowise.inventoryservice.exceptions.NotEnoughProductInInventoryException;
import by.innowise.inventoryservice.exceptions.ProductWithIdExistsException;
import by.innowise.inventoryservice.repositories.InventoryRepository;
import by.innowise.inventoryservice.services.InventoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Validated
@Transactional(readOnly = true)
@Slf4j
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final PositionConverter positionConverter;
    private final ObjectMapper objectMapper;

    private InventoryService inventoryService;


    @Autowired
    public void setInventoryService(@Lazy InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @KafkaListener(topics = {"${topic.inventory-change}"})
    @Transactional(propagation = Propagation.NEVER,
            rollbackFor = {ProductWithIdExistsException.class, NoProductWithIdExistsException.class,
                    NotEnoughProductInInventoryException.class})
    protected void inventoryChange(ConsumerRecord<String, InventoryChangeDto> record)
            throws ProductWithIdExistsException, NoProductWithIdExistsException, NotEnoughProductInInventoryException {
        switch (record.value().getActionDto()) {
            case ADD_POSITION -> inventoryService.addPosition(UUID.fromString((String) record.value().getData()));
            case DELETE_POSITION -> inventoryService.deletePosition(UUID.fromString((String) record.value().getData()));
            case ADD_AMOUNT -> inventoryService.addPositionAmount(objectMapper.convertValue(
                    record.value().getData(), PositionDto.class));
            case SUB_AMOUNT -> inventoryService.subPositionAmount(objectMapper.convertValue(
                    record.value().getData(), PositionDto.class));
        }
    }

    @Override
    @Transactional(rollbackFor = ProductWithIdExistsException.class)
    public void addPosition(UUID productId) throws ProductWithIdExistsException {
        if (inventoryRepository.findById(productId).isPresent()) {
            log.warn(Messages.PRODUCT_WITH_ID_EXISTS);
            throw new ProductWithIdExistsException(Messages.PRODUCT_WITH_ID_EXISTS);
        }
        inventoryRepository.save(Position.builder().
                productId(productId).
                amount(0).
                build());
    }

    @Override
    @Transactional(rollbackFor = NoProductWithIdExistsException.class)
    public void deletePosition(UUID productId) throws NoProductWithIdExistsException {
        inventoryRepository.findById(productId).orElseThrow(() -> {
            log.warn(Messages.NO_PRODUCT_WITH_ID_EXISTS);
            return new NoProductWithIdExistsException(Messages.NO_PRODUCT_WITH_ID_EXISTS);
        });
        inventoryRepository.deleteById(productId);
    }

    @Override
    public Optional<PositionDto> readPosition(UUID productId) {
        return inventoryRepository.findById(productId).map(positionConverter::toDto);
    }

    @Override
    public List<PositionDto> readAllPositions() {
        return inventoryRepository.findAll().stream().map(positionConverter::toDto).toList();
    }

    @Override
    @Transactional(rollbackFor = NoProductWithIdExistsException.class)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "jakarta.persistence.lock.timeout", value = "2000")})
    public UpdateProductAmountResponseDto addPositionAmount(PositionDto positionDto)
            throws NoProductWithIdExistsException {
        Position position = inventoryRepository.findById(positionDto.getProductId()).orElseThrow(() ->
                new NoProductWithIdExistsException(Messages.NO_PRODUCT_WITH_ID_EXISTS));
        Integer updatedAmount = position.getAmount() + positionDto.getAmount();
        inventoryRepository.save(Position.builder().
                productId(positionDto.getProductId()).
                amount(updatedAmount).
                build());
        return UpdateProductAmountResponseDto.builder().updatedAmount(updatedAmount).build();
    }

    @Override
    @Transactional(rollbackFor = {NoProductWithIdExistsException.class,
            NotEnoughProductInInventoryException.class})
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "jakarta.persistence.lock.timeout", value = "2000")})
    public UpdateProductAmountResponseDto subPositionAmount(PositionDto positionDto)
            throws NoProductWithIdExistsException, NotEnoughProductInInventoryException {
        Position position = inventoryRepository.findById(positionDto.getProductId()).orElseThrow(() ->
                new NoProductWithIdExistsException(Messages.NO_PRODUCT_WITH_ID_EXISTS));
        if (position.getAmount() < positionDto.getAmount()) {
            throw new NotEnoughProductInInventoryException(Messages.NOT_ENOUGH_PRODUCT_IN_INVENTORY);
        }
        Integer updatedAmount = position.getAmount() - positionDto.getAmount();
        inventoryRepository.save(Position.builder().
                productId(positionDto.getProductId()).
                amount(updatedAmount).
                build());
        return UpdateProductAmountResponseDto.builder().updatedAmount(updatedAmount).build();
    }
}
