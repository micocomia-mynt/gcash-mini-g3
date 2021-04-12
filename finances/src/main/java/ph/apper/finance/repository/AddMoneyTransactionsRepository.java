package ph.apper.finance.repository;


import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ph.apper.finance.domain.AddMoneyTransaction;

import java.util.Optional;

@Repository
public interface AddMoneyTransactionsRepository extends CrudRepository<AddMoneyTransaction, String> {
    Optional<AddMoneyTransaction> findById(String accountId);
}