package ph.apper.account.exception;

public class AccountAuthenticationException extends Exception {
    public AccountAuthenticationException() {
        super();
    }

    public AccountAuthenticationException(String message) {
        super(message);
    }

    public AccountAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }

    public AccountAuthenticationException(Throwable cause) {
        super(cause);
    }

    protected AccountAuthenticationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
