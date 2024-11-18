package by.innowise.userservice.filters;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
@Getter
@Setter
public class Context {
    public static final String AUTHORIZATION = "Authorization";

    private String authToken = "";
    private UUID userId;
    public Map<String, String> getHeaders() {
        return Map.of(AUTHORIZATION, authToken);
    }
}
