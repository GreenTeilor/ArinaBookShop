package by.innowise.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@NoArgsConstructor
@Data
public class UserRequestDto {

    @Email(message = "Invalid email")
    @NotNull(message = "Null not allowed")
    @NotBlank(message = "Blank is not allowed")
    private String email;

    @Size(min = 8, message = "Password has to contain at least 8 characters")
    @Pattern(regexp = "^(?=.*\\d)(?=.*\\D).+$",
            message = "Password has to contain at least 1 digit and 1 other character")
    @NotNull(message = "Null not allowed")
    private String password;
}
