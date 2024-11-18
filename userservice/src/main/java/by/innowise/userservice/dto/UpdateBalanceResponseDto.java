package by.innowise.userservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@SuperBuilder
@NoArgsConstructor
@Data
public class UpdateBalanceResponseDto {
    BigDecimal updatedBalance;
}
