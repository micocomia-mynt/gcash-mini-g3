package ph.apper.finance.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Data
@Entity
public class TransferTransaction {

    @Id
    private String transactionId;
    public TransferTransaction(String transactionId) {
        this.transactionId = transactionId;
    }
    public TransferTransaction() {
    }

    private String fromAccountId;
    private String toAccountId;
    private Boolean transactionSuccess;
    private Double amount;

    private LocalDateTime timestamp;

}

