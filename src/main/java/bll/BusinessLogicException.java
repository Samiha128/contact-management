package bll;

public class BusinessLogicException extends Exception {
    public BusinessLogicException() {
    }
    public BusinessLogicException(String message) {
        super(message);
    }
    public BusinessLogicException(String message, Throwable cause) {
        super(message, cause);
    }

}
