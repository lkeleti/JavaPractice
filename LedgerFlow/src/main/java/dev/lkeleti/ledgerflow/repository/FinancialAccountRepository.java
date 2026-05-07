package dev.lkeleti.ledgerflow.repository;

import dev.lkeleti.ledgerflow.entity.FinancialAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FinancialAccountRepository
        extends JpaRepository<FinancialAccount, Long> {

    boolean existsByAccountNumber(String accountNumber);
}