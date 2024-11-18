package by.innowise.orderservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class OrderService {
    public static void main( String[] args ) {
        SpringApplication.run(OrderService.class, args);
    }
}
