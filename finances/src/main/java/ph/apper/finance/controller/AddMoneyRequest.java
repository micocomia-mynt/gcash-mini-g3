package ph.apper.finance.controller;

import lombok.Data;

@Data
public class AddMoneyRequest {
    private String accountId;
    private Double amount;


    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}
