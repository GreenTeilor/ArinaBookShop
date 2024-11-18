package by.innowise.userservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@NoArgsConstructor
@Data
public class ErrorDto {
    private String code;
    private Object value;
}
