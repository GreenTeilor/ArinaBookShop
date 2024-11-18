package by.innowise.orderservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@SuperBuilder
@NoArgsConstructor
@Data
public class OrderResponseDto {
    private UUID id;
    private UUID userId;
    private OrderStatusDto status;
    private BigDecimal price;
    private List<OrderProductResponseDto> products;
}
