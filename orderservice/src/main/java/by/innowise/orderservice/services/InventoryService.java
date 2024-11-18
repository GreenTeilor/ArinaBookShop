package by.innowise.orderservice.services;

import by.innowise.orderservice.dto.PositionDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@FeignClient("inventory-service")
@Service
public interface InventoryService {
    @RequestMapping(
            method = RequestMethod.GET,
            value = "/inventory/{id}",
            consumes = "application/json"
    )
    Optional<PositionDto> readPosition(@RequestHeader Map<String, String> headers,
                                      @PathVariable UUID id);
}
