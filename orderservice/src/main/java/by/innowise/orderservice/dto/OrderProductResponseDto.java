package by.innowise.orderservice.dto;

import by.innowise.orderservice.dto.products.BookDto;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@NoArgsConstructor
@Data
public class OrderProductResponseDto {
    private BookDto product;
    private Integer amount;
}
