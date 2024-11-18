package by.innowise.orderservice.dto.fiegnerrors;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface HandleFeignError {
    Class<? extends FeignHttpExceptionHandler> value();
}
