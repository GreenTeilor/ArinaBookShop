package by.innowise.orderservice.services;

import by.innowise.orderservice.dto.UpdateBalanceResponseDto;
import by.innowise.orderservice.dto.fiegnerrors.HandleFeignError;
import by.innowise.orderservice.dto.fiegnerrors.ChangeUserBalanceExceptionHandler;
import by.innowise.orderservice.exceptions.InsufficientFundsException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.Map;

@FeignClient("user-service")
@Service
public interface UserService {
    @RequestMapping(
            method = RequestMethod.POST,
            value = "/user/self/writeOffFunds",
            consumes = "application/json"
    )
    @HandleFeignError(ChangeUserBalanceExceptionHandler.class)
    UpdateBalanceResponseDto writeOffFunds(@RequestHeader Map<String, String> headers, @RequestParam BigDecimal amount)
            throws InsufficientFundsException;
}
