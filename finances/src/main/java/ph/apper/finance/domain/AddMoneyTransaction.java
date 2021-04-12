package ph.apper.finance.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
public class AddMoneyTransaction {

    @Id
    private String transactionId;
    public AddMoneyTransaction(String transactionId) {
        this.transactionId = transactionId;
    }
    public AddMoneyTransaction() {
    }

    private String AccountId;
    private Boolean transactionSuccess;
    private Double amount;

    private LocalDateTime timestamp;

}

