package ph.apper.finance.controller;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferRequest{
    private String fromAccountId;
    private String toAccountId;
    private Double amount;


    public String getFromAccountId() {
        return fromAccountId;
    }

    public void setFromAccountId(String fromAccountId) {
        this.fromAccountId = fromAccountId;
    }

    public String getToAccountId() {
        return toAccountId;
    }

    public void setToAccountId(String toAccountId) {
        this.toAccountId = toAccountId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}
