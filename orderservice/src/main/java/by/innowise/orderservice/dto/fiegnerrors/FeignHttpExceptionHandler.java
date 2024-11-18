package by.innowise.orderservice.dto.fiegnerrors;

import feign.Response;

public interface FeignHttpExceptionHandler {
    Exception handle(Response response);
}