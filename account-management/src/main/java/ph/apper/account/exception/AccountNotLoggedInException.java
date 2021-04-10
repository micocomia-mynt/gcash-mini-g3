package ph.apper.account.exception;

public class AccountNotLoggedInException extends Exception{
    public AccountNotLoggedInException(String message) {
        super(message);
    }

    public AccountNotLoggedInException(String message, Throwable cause) {
        super(message, cause);}
}
