package ph.apper.account.domain;

import lombok.Data;

@Data
public class Account {
    private String firstName;
    private String lastName;
    private String password;
    private String accountId;
    private float balance;
    private boolean loggedIn;
}
