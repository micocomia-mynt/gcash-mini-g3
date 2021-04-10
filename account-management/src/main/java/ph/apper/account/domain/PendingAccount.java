package ph.apper.account.domain;

import lombok.Data;

@Data
public class PendingAccount extends Account {
    private String verificationCode;
}
