package by.innowise.orderservice.converters;

import by.innowise.orderservice.domain.OrderStatus;
import by.innowise.orderservice.dto.OrderStatusDto;
import by.innowise.orderservice.dto.converters.OrderStatusConverter;
import by.innowise.orderservice.dto.converters.OrderStatusConverterImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class OrderStatusConverterTest {

    private final OrderStatusConverter orderStatusConverter = new OrderStatusConverterImpl();
    private static OrderStatusDto orderStatusDto;
    private static OrderStatus orderStatus;

    @BeforeAll
    public static void setUp() {
        orderStatusDto = OrderStatusDto.CREATED;
        orderStatus = OrderStatus.CREATED;
    }

    @Test
    public void toDto() {
        Assertions.assertEquals(orderStatusConverter.toDto(orderStatus), orderStatusDto);
    }

    @Test
    public void fromDto() {
        Assertions.assertEquals(orderStatusConverter.fromDto(orderStatusDto), orderStatus);
    }
}
