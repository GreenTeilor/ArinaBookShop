package by.innowise.gatewayservice.filters;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.TraceContext;
import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Configuration
@RequiredArgsConstructor
public class ResponseFilter {

    private final Tracer tracer;

    @Bean
    public GlobalFilter postGlobalFilter() {
        return (exchange, chain) -> {
            String traceId = Optional.ofNullable(tracer.currentSpan())
                    .map(Span::context)
                    .map(TraceContext::traceId)
                    .orElse("null");

            return chain.filter(exchange).then(Mono.fromRunnable(() ->
                    exchange.getResponse().getHeaders().add("correlationId", traceId)));
        };
    }
}
