package ph.apper.account.exception;

public class AccountRegistrationException extends Exception {
    public AccountRegistrationException() {
        super();
    }

    public AccountRegistrationException(String message) {
        super(message);
    }

    public AccountRegistrationException(String message, Throwable cause) {
        super(message, cause);
    }

    public AccountRegistrationException(Throwable cause) {
        super(cause);
    }

    protected AccountRegistrationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
