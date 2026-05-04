package dev.lkeleti.ledgerflow.repository;

import dev.lkeleti.ledgerflow.entity.MoneyTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MoneyTransactionRepository extends JpaRepository<MoneyTransaction, Long> {
}