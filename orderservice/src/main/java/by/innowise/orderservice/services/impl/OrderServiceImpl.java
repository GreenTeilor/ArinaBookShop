package by.innowise.orderservice.services.impl;

import by.innowise.orderservice.domain.Order;
import by.innowise.orderservice.domain.OrderProductId;
import by.innowise.orderservice.domain.OrderStatus;
import by.innowise.orderservice.domain.OrdersProducts;
import by.innowise.orderservice.dto.ActionDto;
import by.innowise.orderservice.dto.UpdateOrderStatusResponseDto;
import by.innowise.orderservice.dto.converters.OrderStatusConverter;
import by.innowise.orderservice.dto.InventoryChangeDto;
import by.innowise.orderservice.dto.OrderProductResponseDto;
import by.innowise.orderservice.dto.OrderRequestDto;
import by.innowise.orderservice.dto.OrderResponseDto;
import by.innowise.orderservice.dto.OrderStatusDto;
import by.innowise.orderservice.dto.PositionDto;
import by.innowise.orderservice.dto.products.BookDto;
import by.innowise.orderservice.exceptions.InsufficientFundsException;
import by.innowise.orderservice.exceptions.Messages;
import by.innowise.orderservice.exceptions.NoOrderWithIdExistsException;
import by.innowise.orderservice.exceptions.NoProductWithIdExistsException;
import by.innowise.orderservice.exceptions.NoProductsInOrderException;
import by.innowise.orderservice.exceptions.NotEnoughProductsInInventoryException;
import by.innowise.orderservice.exceptions.UserNotOrderOwnerException;
import by.innowise.orderservice.filters.ContextHolder;
import by.innowise.orderservice.repositories.OrderRepository;
import by.innowise.orderservice.repositories.OrdersProductsRepository;
import by.innowise.orderservice.services.InventoryService;
import by.innowise.orderservice.services.OrderService;
import by.innowise.orderservice.services.ProductService;
import by.innowise.orderservice.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Validated
@Transactional(readOnly = true)
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final ProductService productService;
    private final InventoryService inventoryService;
    private final OrderRepository orderRepository;
    private final UserService userService;
    private final OrdersProductsRepository ordersProductsRepository;
    private final OrderStatusConverter orderStatusConverter;
    private final KafkaTemplate<String, InventoryChangeDto> kafkaTemplate;

    private OrderServiceImpl orderService;

    @Autowired
    public void setInventoryService(@Lazy OrderServiceImpl orderService) {
        this.orderService = orderService;
    }

    @Value("${topic.inventory-change}")
    private String inventoryChangeTopic;

    @Transactional(propagation = Propagation.MANDATORY)
    protected void sendInventoryChange(InventoryChangeDto inventoryChangeDto) {
        ProducerRecord<String, InventoryChangeDto> record = new ProducerRecord<>(inventoryChangeTopic, null, System.currentTimeMillis(),
                String.valueOf(inventoryChangeDto.getActionDto()), inventoryChangeDto,
                ContextHolder.getContext().getKafkaHeaders());
        kafkaTemplate.send(record);
    }

    @Override
    @Transactional(rollbackFor = {Throwable.class})
    @SuppressWarnings({"unchecked"})
    public OrderResponseDto create(UUID userId, OrderRequestDto orderDto)
            throws NoProductWithIdExistsException, NotEnoughProductsInInventoryException, InsufficientFundsException, NoProductsInOrderException {
        if (orderDto.getProducts().isEmpty()) {
            throw new NoProductsInOrderException(Messages.NO_PRODUCTS_IN_ORDER);
        }
        Set<UUID> productsId = orderDto.getProducts().keySet();
        List<BookDto> products = productsId.stream().
                map(id -> productService.read(id).orElse(null)).toList();
        if (products.stream().anyMatch(Objects::isNull)) {
            throw new NoProductWithIdExistsException(Messages.NO_PRODUCT_WITH_ID_EXISTS);
        }
        Order order = orderRepository.save(Order.builder().
                userId(userId).
                status(OrderStatus.CREATED).
                price(products.stream().
                        reduce(BigDecimal.ZERO,
                                (x, y) -> x.add(y.getPrice().multiply(
                                        BigDecimal.valueOf(orderDto.getProducts().get(y.getId())))),
                                BigDecimal::add)).
                build());
        orderDto.getProducts().forEach((key, value) ->
                ordersProductsRepository.save(OrdersProducts.builder().
                        id(new OrderProductId(order.getId(), key)).
                        amount(value).
                        build()));
        if (productsId.stream().anyMatch(id -> inventoryService.readPosition(
                ContextHolder.getContext().getHeaders(), id).get().getAmount()
                < orderDto.getProducts().get(id))) {
            throw new NotEnoughProductsInInventoryException(Messages.NOT_ENOUGH_PRODUCTS_IN_INVENTORY);
        }
        orderDto.getProducts().forEach((key, value) ->
                orderService.sendInventoryChange(InventoryChangeDto.builder().
                        actionDto(ActionDto.SUB_AMOUNT).
                        data(PositionDto.builder().
                                productId(key).
                                amount(value).
                                build()).
                        build()));
        userService.writeOffFunds(ContextHolder.getContext().getHeaders(), order.getPrice());
        return OrderResponseDto.builder().
                id(order.getId()).
                userId(userId).
                status(OrderStatusDto.CREATED).
                price(order.getPrice()).
                products((List<OrderProductResponseDto>) products.stream().map(p -> OrderProductResponseDto.builder().
                        product(p).
                        amount(orderDto.getProducts().get(p.getId())).
                        build()).toList()).
                build();
    }

    @Override
    @Transactional(rollbackFor = {NoOrderWithIdExistsException.class})
    public UUID delete(UUID id) throws NoOrderWithIdExistsException {
        orderRepository.findById(id).orElseThrow(() ->
                new NoOrderWithIdExistsException(Messages.NO_ORDER_WITH_ID_EXISTS));
        orderRepository.deleteById(id);
        ordersProductsRepository.deleteAllByIdOrderId(id);
        log.warn("Order with id " + id + " deleted");
        return id;
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public Optional<OrderResponseDto> read(UUID id) throws UserNotOrderOwnerException {
        Order order = orderRepository.findById(id).orElse(null);
        if (order == null) {
            return Optional.empty();
        }
        if (!order.getUserId().equals(ContextHolder.getContext().getUserId())) {
            throw new UserNotOrderOwnerException(Messages.USER_NOT_ORDER_OWNER);
        }
        List<OrdersProducts> ordersProducts = ordersProductsRepository.findAllByIdOrderId(id);
        return Optional.ofNullable(OrderResponseDto.builder().
                id(id).
                userId(order.getUserId()).
                status(orderStatusConverter.toDto(order.getStatus())).
                price(order.getPrice()).
                products((List<OrderProductResponseDto>) ordersProducts.stream().map(op ->
                        OrderProductResponseDto.builder().
                                product(productService.read(op.getId().getProductId()).get()).
                                amount(op.getAmount()).
                                build()).toList()).
                build());
    }

    @Override
    @Transactional(rollbackFor = {NoOrderWithIdExistsException.class})
    public UpdateOrderStatusResponseDto updateStatus(UUID id, OrderStatusDto statusDto) throws NoOrderWithIdExistsException {
        Order order = orderRepository.findById(id).orElseThrow(() ->
                new NoOrderWithIdExistsException(Messages.NO_ORDER_WITH_ID_EXISTS));
        order.setStatus(orderStatusConverter.fromDto(statusDto));
        return UpdateOrderStatusResponseDto.builder().
                updatedStatus(statusDto).
                build();
    }
}
