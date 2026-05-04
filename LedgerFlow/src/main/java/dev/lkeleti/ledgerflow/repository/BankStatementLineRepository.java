package dev.lkeleti.ledgerflow.repository;

import dev.lkeleti.ledgerflow.entity.BankStatementLine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankStatementLineRepository extends JpaRepository<BankStatementLine, Long> {
}