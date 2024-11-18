package by.innowise.orderservice.dto.converters;

import by.innowise.orderservice.domain.OrderStatus;
import by.innowise.orderservice.dto.OrderStatusDto;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface OrderStatusConverter {
    OrderStatus fromDto(OrderStatusDto orderStatusDto);
    OrderStatusDto toDto(OrderStatus orderStatus);
}
