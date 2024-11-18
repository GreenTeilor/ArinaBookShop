package by.innowise.inventoryservice.exceptions;

import by.innowise.inventoryservice.dto.ErrorDto;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.stream.StreamSupport;

@RestControllerAdvice
public class GlobalControllerExceptionHandler {
    @ExceptionHandler(value = {ProductWithIdExistsException.class, NotEnoughProductInInventoryException.class})
    protected ResponseEntity<ErrorDto> handleBadRequestsExceptions(Exception e) {
        return new ResponseEntity<>(ErrorDto.builder().
                code(String.valueOf(HttpStatus.BAD_REQUEST.value())).
                value(e.getMessage()).
                build(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {NoProductWithIdExistsException.class})
    protected ResponseEntity<ErrorDto> handleNotFoundExceptions(Exception e) {
        return new ResponseEntity<>(ErrorDto.builder().
                code(String.valueOf(HttpStatus.NOT_FOUND.value())).
                value(e.getMessage()).
                build(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {MissingServletRequestParameterException.class})
    protected ResponseEntity<ErrorDto> handleMissingRequestParameter() {
        return new ResponseEntity<>(ErrorDto.builder().
                code(String.valueOf(HttpStatus.BAD_REQUEST.value())).
                value("Request parameter is missing").
                build(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {HttpMessageNotReadableException.class})
    protected ResponseEntity<ErrorDto> handleMissingRequestBody() {
        return new ResponseEntity<>(ErrorDto.builder().
                code(String.valueOf(HttpStatus.BAD_REQUEST.value())).
                value("Request body is missing").
                build(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {MethodArgumentTypeMismatchException.class})
    protected ResponseEntity<ErrorDto> handleMethodArgumentTypeMismatchException() {
        return new ResponseEntity<>(ErrorDto.builder().
                code(String.valueOf(HttpStatus.BAD_REQUEST.value())).
                value("Argument type mismatch").
                build(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {ConstraintViolationException.class})
    protected ResponseEntity<ErrorDto> handleConstraintViolationException(ConstraintViolationException e) {
        Multimap<String, String> violations = ArrayListMultimap.create();
        e.getConstraintViolations().forEach(v -> violations.put(
                StreamSupport.stream(v.getPropertyPath().spliterator(), false)
                        .map(Path.Node::getName)
                        .reduce((first, second) -> second)
                        .orElseGet(() -> v.getPropertyPath().toString()),
                v.getMessage()));
        return new ResponseEntity<>(ErrorDto.builder().
                code(String.valueOf(HttpStatus.BAD_REQUEST.value())).
                value(violations.asMap()).
                build(), HttpStatus.BAD_REQUEST);
    }
}
