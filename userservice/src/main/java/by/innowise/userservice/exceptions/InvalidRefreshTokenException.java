package by.innowise.userservice.exceptions;

public class InvalidRefreshTokenException extends Exception {
    public InvalidRefreshTokenException(String message) {
        super(message);
    }
}
