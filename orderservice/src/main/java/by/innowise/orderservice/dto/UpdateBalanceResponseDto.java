package by.innowise.orderservice.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@SuperBuilder
@NoArgsConstructor
@Data
@EqualsAndHashCode
public class UpdateBalanceResponseDto {
    BigDecimal updatedBalance;
}
