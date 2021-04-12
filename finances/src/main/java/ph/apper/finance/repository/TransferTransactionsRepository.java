package ph.apper.finance.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ph.apper.finance.domain.TransferTransaction;

import java.util.Optional;

@Repository
public interface TransferTransactionsRepository extends CrudRepository<TransferTransaction, String> {
    Optional<TransferTransaction> findById(String accountId);
}
