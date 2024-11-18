package by.innowise.orderservice.services;

import by.innowise.orderservice.domain.Order;
import by.innowise.orderservice.domain.OrderStatus;
import by.innowise.orderservice.dto.InventoryChangeDto;
import by.innowise.orderservice.dto.OrderProductResponseDto;
import by.innowise.orderservice.dto.OrderRequestDto;
import by.innowise.orderservice.dto.OrderResponseDto;
import by.innowise.orderservice.dto.OrderStatusDto;
import by.innowise.orderservice.dto.PositionDto;
import by.innowise.orderservice.dto.converters.OrderStatusConverter;
import by.innowise.orderservice.dto.products.BookDto;
import by.innowise.orderservice.exceptions.InsufficientFundsException;
import by.innowise.orderservice.exceptions.NoProductWithIdExistsException;
import by.innowise.orderservice.exceptions.NoProductsInOrderException;
import by.innowise.orderservice.exceptions.NotEnoughProductsInInventoryException;
import by.innowise.orderservice.repositories.OrderRepository;
import by.innowise.orderservice.repositories.OrdersProductsRepository;
import by.innowise.orderservice.services.impl.OrderServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class OrderServiceTest {

    @Mock
    private ProductService productService;

    @Mock
    private InventoryService inventoryService;

    @Mock
    private UserService userService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    @SuppressWarnings("unused")
    private OrdersProductsRepository ordersProductsRepository;

    @Mock
    @SuppressWarnings("unused")
    private OrderStatusConverter orderStatusConverter;

    @Mock
    @SuppressWarnings("unused")
    private OrderService innerOrderService;

    @Mock
    @SuppressWarnings("unused")
    private KafkaTemplate<String, InventoryChangeDto> kafkaTemplate;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    public void createOrderWithNotExistingProduct() {
        UUID id1 = UUID.fromString("439cba0c-09f6-43cf-bdf0-873eab388b6c");
        UUID id2 = UUID.fromString("0f390e29-34d1-450b-a1df-137929e46e79");
        OrderRequestDto orderRequestDto = OrderRequestDto.builder().
                products(Map.of(id1, 3, id2, 4)).
                build();
        Mockito.when(productService.read(id1)).thenReturn(Optional.of(BookDto.builder().
                build()));
        Mockito.when(productService.read(id2)).thenReturn(Optional.empty());
        Assertions.assertThrows(NoProductWithIdExistsException.class, () -> orderService.create(
                UUID.fromString("0f588a5a-bb49-4d84-910a-e80ab4ce2876"),
                orderRequestDto));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void createOrderHavingNotEnoughProductsInInventory() {
        UUID id1 = UUID.fromString("439cba0c-09f6-43cf-bdf0-873eab388b6c");
        UUID id2 = UUID.fromString("0f390e29-34d1-450b-a1df-137929e46e79");
        UUID userId = UUID.randomUUID();
        Mockito.when(productService.read(id1)).thenReturn(Optional.of(BookDto.builder().
                id(id1).
                price(BigDecimal.valueOf(3.48)).
                build()));
        Mockito.when(productService.read(id2)).thenReturn(Optional.of(BookDto.builder().
                id(id2).
                price(BigDecimal.valueOf(7.56)).
                build()));
        Mockito.when(inventoryService.readPosition(
                any(Map.class), eq(id1))).thenReturn(Optional.of(PositionDto.builder().
                productId(id1).
                amount(2).
                build()));
        Mockito.when(inventoryService.readPosition(
                any(Map.class), eq(id2))).thenReturn(Optional.of(PositionDto.builder().
                productId(id2).
                amount(5).
                build()));
        OrderRequestDto orderRequestDto = OrderRequestDto.builder().
                products(Map.of(id1, 3, id2, 4)).
                build();
        Mockito.when(orderRepository.save(any(Order.class))).thenReturn(Order.builder().
                id(UUID.randomUUID()).
                userId(userId).
                status(OrderStatus.CREATED).
                price(BigDecimal.valueOf(3.48 + 7.56)).
                build());
        Assertions.assertThrows(NotEnoughProductsInInventoryException.class, () -> orderService.create(
                userId,
                orderRequestDto));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void createOrderWithInsufficientFunds() throws InsufficientFundsException, NoSuchFieldException,
            IllegalAccessException {
        UUID id1 = UUID.fromString("439cba0c-09f6-43cf-bdf0-873eab388b6c");
        UUID id2 = UUID.fromString("0f390e29-34d1-450b-a1df-137929e46e79");
        UUID userId = UUID.randomUUID();
        Mockito.when(productService.read(id1)).thenReturn(Optional.of(BookDto.builder().
                id(id1).
                price(BigDecimal.valueOf(3.48)).
                build()));
        Mockito.when(productService.read(id2)).thenReturn(Optional.of(BookDto.builder().
                id(id2).
                price(BigDecimal.valueOf(7.56)).
                build()));
        Mockito.when(inventoryService.readPosition(
                any(Map.class), eq(id1))).thenReturn(Optional.of(PositionDto.builder().
                productId(id1).
                amount(3).
                build()));
        Mockito.when(inventoryService.readPosition(
                any(Map.class), eq(id2))).thenReturn(Optional.of(PositionDto.builder().
                productId(id2).
                amount(8).
                build()));
        OrderRequestDto orderRequestDto = OrderRequestDto.builder().
                products(Map.of(id1, 3, id2, 4)).
                build();
        Mockito.when(orderRepository.save(any(Order.class))).thenReturn(Order.builder().
                id(UUID.randomUUID()).
                userId(userId).
                status(OrderStatus.CREATED).
                price(BigDecimal.valueOf(3.48 + 7.56)).
                build());
        ReflectionTestUtils.setField(orderService, "orderService", orderService);
        Class<OrderServiceImpl> clazz = OrderServiceImpl.class;
        Field bookServiceField = clazz.getDeclaredField("orderService");
        bookServiceField.setAccessible(true);
        OrderServiceImpl innerService = (OrderServiceImpl)bookServiceField.get(orderService);
        Field inventoryChangeTopicField = clazz.getDeclaredField("inventoryChangeTopic");
        inventoryChangeTopicField.setAccessible(true);
        inventoryChangeTopicField.set(innerService, "some-topic");
        Mockito.when(userService.writeOffFunds(any(Map.class),
                eq(BigDecimal.valueOf(3.48 + 7.56)))).thenThrow(InsufficientFundsException.class);
        Assertions.assertThrows(InsufficientFundsException.class, () -> orderService.create(
                userId,
                orderRequestDto));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void createOrderSuccessfully() throws NoSuchFieldException, IllegalAccessException,
            NotEnoughProductsInInventoryException, NoProductWithIdExistsException, InsufficientFundsException, NoProductsInOrderException {
        UUID id1 = UUID.fromString("439cba0c-09f6-43cf-bdf0-873eab388b6c");
        UUID id2 = UUID.fromString("0f390e29-34d1-450b-a1df-137929e46e79");
        UUID userId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        BookDto book1 = BookDto.builder().
                id(id1).
                title("title1").
                genre("genre1").
                author("author1").
                publicationYear(1978).
                weight(BigDecimal.valueOf(0.5)).
                price(BigDecimal.valueOf(78.99)).
                build();
        BookDto book2 = BookDto.builder().
                id(id2).
                title("title2").
                genre("genre2").
                author("author2").
                publicationYear(1978).
                weight(BigDecimal.valueOf(0.5)).
                price(BigDecimal.valueOf(78.99)).
                build();
        Mockito.when(productService.read(id1)).thenReturn(Optional.of(book1));
        Mockito.when(productService.read(id2)).thenReturn(Optional.of(book2));
        Mockito.when(inventoryService.readPosition(
                any(Map.class), eq(id1))).thenReturn(Optional.of(PositionDto.builder().
                productId(id1).
                amount(3).
                build()));
        Mockito.when(inventoryService.readPosition(
                any(Map.class), eq(id2))).thenReturn(Optional.of(PositionDto.builder().
                productId(id2).
                amount(8).
                build()));
        OrderRequestDto orderRequestDto = OrderRequestDto.builder().
                products(Map.of(id1, 3, id2, 4)).
                build();
        Mockito.when(orderRepository.save(any(Order.class))).thenReturn(Order.builder().
                id(orderId).
                userId(userId).
                status(OrderStatus.CREATED).
                price(BigDecimal.valueOf(3.48 + 7.56)).
                build());
        ReflectionTestUtils.setField(orderService, "orderService", orderService);
        Class<OrderServiceImpl> clazz = OrderServiceImpl.class;
        Field bookServiceField = clazz.getDeclaredField("orderService");
        bookServiceField.setAccessible(true);
        OrderServiceImpl innerService = (OrderServiceImpl)bookServiceField.get(orderService);
        Field inventoryChangeTopicField = clazz.getDeclaredField("inventoryChangeTopic");
        inventoryChangeTopicField.setAccessible(true);
        inventoryChangeTopicField.set(innerService, "some-topic");
        OrderProductResponseDto p1 = OrderProductResponseDto.builder().
                product(book1).
                amount(3).
                build();
        OrderProductResponseDto p2 = OrderProductResponseDto.builder().
                product(book2).
                amount(4).
                build();
        OrderResponseDto orderResponseDto = OrderResponseDto.builder().
                id(orderId).
                userId(userId).
                status(OrderStatusDto.CREATED).
                price(BigDecimal.valueOf(3.48 + 7.56)).
                products(List.of(p1, p2)).
                build();
        OrderResponseDto createdOrder = orderService.create(userId, orderRequestDto);
        //Such redundant check, as lists of products in orders can't be compared directly with equals, as
        //orderService.create() returns order products not in the order they were passed
        Assertions.assertTrue(orderResponseDto.getId().equals(createdOrder.getId()) && orderResponseDto.getStatus().
                equals(createdOrder.getStatus()) && orderResponseDto.getPrice().equals(createdOrder.getPrice()) &&
                orderResponseDto.getUserId().equals(createdOrder.getUserId()) && orderResponseDto.getProducts().
                containsAll(createdOrder.getProducts()));
    }
}
