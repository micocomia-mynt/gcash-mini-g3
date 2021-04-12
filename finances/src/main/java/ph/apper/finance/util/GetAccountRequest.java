package ph.apper.finance.util;

import lombok.Data;

@Data
public class GetAccountRequest {
    private String accountId;

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }
}
