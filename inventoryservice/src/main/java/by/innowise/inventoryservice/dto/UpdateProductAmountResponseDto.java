package by.innowise.inventoryservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@NoArgsConstructor
@Data
public class UpdateProductAmountResponseDto {
    private Integer updatedAmount;
}
