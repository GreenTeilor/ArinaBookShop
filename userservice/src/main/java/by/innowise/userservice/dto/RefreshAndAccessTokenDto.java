package by.innowise.userservice.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@NoArgsConstructor
@Data
public class RefreshAndAccessTokenDto {
    @JsonAlias("refresh_token")
    private String refreshToken;

    @JsonAlias("access_token")
    private String accessToken;
}
