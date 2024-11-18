package by.innowise.userservice.exceptions;

import by.innowise.userservice.dto.ErrorDto;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.apache.hc.core5.http.NoHttpResponseException;
import org.postgresql.util.PSQLException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.net.ConnectException;

import java.util.stream.StreamSupport;

@RestControllerAdvice
public class GlobalControllerExceptionHandler {
    @ExceptionHandler(value = {NoUserExistsException.class, NoRoleExistsException.class})
    protected ResponseEntity<ErrorDto> handleNotFoundExceptions(Exception e) {
        return new ResponseEntity<>(ErrorDto.builder().
                code(String.valueOf(HttpStatus.NOT_FOUND.value())).
                value(e.getMessage()).
                build(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {UserEmailExistsException.class, InsufficientFundsException.class,
            UserNotCreatedException.class, InvalidCredentialsException.class})
    protected ResponseEntity<ErrorDto> handleBadRequestExceptions(Exception e) {
        return new ResponseEntity<>(ErrorDto.builder().
                code(String.valueOf(HttpStatus.BAD_REQUEST.value())).
                value(e.getMessage()).
                build(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {MissingRequestCookieException.class})
    protected ResponseEntity<ErrorDto> handleMissingRequestCookieException(MissingRequestCookieException e) {
        return new ResponseEntity<>(ErrorDto.builder().
                code(String.valueOf(HttpStatus.BAD_REQUEST.value())).
                value("Missing cookie: " + e.getCookieName()).
                build(), HttpStatus.BAD_REQUEST);
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

    @ExceptionHandler(value = {ConnectException.class})
    protected ResponseEntity<ErrorDto> handleConnectException() {
        return new ResponseEntity<>(ErrorDto.builder().
                code(String.valueOf(HttpStatus.BAD_REQUEST.value())).
                value("Auth provider is unavailable").
                build(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {NoHttpResponseException.class})
    protected ResponseEntity<ErrorDto> handleNoHttpResponseException() {
        return new ResponseEntity<>(ErrorDto.builder().
                code(String.valueOf(HttpStatus.BAD_REQUEST.value())).
                value("Auth provider is unavailable").
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

    @ExceptionHandler(value = {DataIntegrityViolationException.class})
    protected ResponseEntity<ErrorDto> handleDataIntegrityViolationException() {
        return new ResponseEntity<>(ErrorDto.builder().
                code(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value())).
                value("Data integrity violation").
                build(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = {PSQLException.class})
    protected ResponseEntity<ErrorDto> handlePSQLException() {
        return new ResponseEntity<>(ErrorDto.builder().
                code(String.valueOf(HttpStatus.SERVICE_UNAVAILABLE.value())).
                value("Server db is overloaded").
                build(), HttpStatus.SERVICE_UNAVAILABLE);
    }
}

