package ph.apper.account.exception;

public class AccountVerificationException extends Exception {
    public AccountVerificationException() {
        super();
    }

    public AccountVerificationException(String message) {
        super(message);
    }

    public AccountVerificationException(String message, Throwable cause) {
        super(message, cause);
    }

    public AccountVerificationException(Throwable cause) {
        super(cause);
    }

    protected AccountVerificationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
