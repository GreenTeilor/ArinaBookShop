package by.innowise.userservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.UUID;

@SuperBuilder
@NoArgsConstructor
@Data
public class UserResponseDto {
    private UUID id;
    private String email;
    private BigDecimal balance;
}
