package mynt.ian.product.exception;

public class ProductNotFoundException extends Exception{

    public ProductNotFoundException(String s, String message) {
        super(message);
    }

    public ProductNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}
