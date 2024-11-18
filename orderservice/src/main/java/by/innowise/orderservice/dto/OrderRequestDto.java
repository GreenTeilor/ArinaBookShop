package by.innowise.orderservice.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Map;
import java.util.UUID;

@SuperBuilder
@NoArgsConstructor
@Data
public class OrderRequestDto {

    @NotNull(message = "Null not allowed")
    Map<
            @NotNull(message = "Null not allowed")
                    UUID,
            @NotNull(message = "Null not allowed")
            @Min(value = 1, message = "Order can't contain less than 1 product of certain type")
            @Max(value = 100, message = "Order can't contain more than 100 products of certain type")
                    Integer> products;
}
