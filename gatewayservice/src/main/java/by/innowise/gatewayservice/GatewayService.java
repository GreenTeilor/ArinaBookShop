package by.innowise.gatewayservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import reactor.core.publisher.Hooks;

@SpringBootApplication
@RefreshScope
public class GatewayService {
    public static void main( String[] args ) {
        Hooks.enableAutomaticContextPropagation();
        SpringApplication.run(GatewayService.class, args);
    }
}
