package dev.lkeleti.ledgerflow.repository;

import dev.lkeleti.ledgerflow.entity.MoneyTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface MoneyTransactionRepository
        extends JpaRepository<MoneyTransaction, Long>,
        JpaSpecificationExecutor<MoneyTransaction> {
}
