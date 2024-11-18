package by.innowise.userservice.exceptions;

public class UserEmailExistsException extends Exception {
    public UserEmailExistsException(String message) {
        super(message);
    }
}
