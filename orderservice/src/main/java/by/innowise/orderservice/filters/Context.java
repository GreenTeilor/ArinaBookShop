package by.innowise.orderservice.filters;

import lombok.Getter;
import lombok.Setter;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.stereotype.Component;

import java.util.List;
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
    public List<Header> getKafkaHeaders() {
        return List.of(new RecordHeader(Context.AUTHORIZATION, ContextHolder.getContext().getAuthToken().getBytes()));
    }
}
