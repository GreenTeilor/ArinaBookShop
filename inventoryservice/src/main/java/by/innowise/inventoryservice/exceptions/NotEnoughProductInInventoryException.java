package by.innowise.inventoryservice.exceptions;

public class NotEnoughProductInInventoryException extends Exception {
    public NotEnoughProductInInventoryException(String message) {
        super(message);
    }
}
