package dev.lkeleti.ledgerflow.repository;

import dev.lkeleti.ledgerflow.entity.AccountingConfig;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountingConfigRepository extends JpaRepository<AccountingConfig, Long> {
}