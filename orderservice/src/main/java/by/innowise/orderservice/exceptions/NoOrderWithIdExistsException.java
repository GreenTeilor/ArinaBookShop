package by.innowise.orderservice.exceptions;

public class NoOrderWithIdExistsException extends Exception {
    public NoOrderWithIdExistsException(String message) {
        super(message);
    }
}
