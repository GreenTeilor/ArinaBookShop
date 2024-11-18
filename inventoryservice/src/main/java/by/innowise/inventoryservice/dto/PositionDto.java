package by.innowise.inventoryservice.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@SuperBuilder
@NoArgsConstructor
@Data
public class PositionDto {

    @NotNull(message = "Null not allowed")
    private UUID productId;

    @Min(value = 0, message = "Can't be negative")
    @Max(value = 10_000, message = "Can't add or take more than 10_000 product of certain type for once")
    @NotNull(message = "Null not allowed")
    private Integer amount;
}
