package by.innowise.orderservice.exceptions;

public class NotEnoughProductsInInventoryException extends Exception{
    public NotEnoughProductsInInventoryException(String message) {
        super(message);
    }
}
