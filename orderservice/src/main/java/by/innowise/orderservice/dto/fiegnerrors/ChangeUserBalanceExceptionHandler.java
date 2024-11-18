package by.innowise.orderservice.dto.fiegnerrors;

import by.innowise.orderservice.exceptions.InsufficientFundsException;
import by.innowise.orderservice.exceptions.Messages;
import feign.Response;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class ChangeUserBalanceExceptionHandler implements FeignHttpExceptionHandler {
    @Override
    public Exception handle(Response response) {
        HttpStatus httpStatus = HttpStatus.resolve(response.status());
        if (HttpStatus.BAD_REQUEST.equals(httpStatus)) {
            return new InsufficientFundsException(Messages.INSUFFICIENT_FUNDS);
        }
        return new RuntimeException(Messages.UNKNOWN_ERROR);
    }
}
