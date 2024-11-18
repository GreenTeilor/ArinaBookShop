package by.innowise.productservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@NoArgsConstructor
@Data
public class InventoryChangeDto {
    private ActionDto actionDto;
    private Object data;
}
