package by.innowise.orderservice.exceptions;

public interface Messages {
    String NO_PRODUCT_WITH_ID_EXISTS = "No product with passed id exists";
    String NOT_ENOUGH_PRODUCTS_IN_INVENTORY = "Not enough products in inventory";
    String NO_ORDER_WITH_ID_EXISTS = "No order with passed id exists";
    String INSUFFICIENT_FUNDS = "Insufficient funds";
    String NO_PRODUCTS_IN_ORDER = "No products in order";
    String UNKNOWN_ERROR = "Unknown error";
    String USER_NOT_ORDER_OWNER = "User is not order owner";
}
