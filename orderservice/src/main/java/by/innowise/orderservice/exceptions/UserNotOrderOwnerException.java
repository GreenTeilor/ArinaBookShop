package by.innowise.orderservice.exceptions;

public class UserNotOrderOwnerException extends Exception {
    public UserNotOrderOwnerException(String message) {
        super(message);
    }
}
