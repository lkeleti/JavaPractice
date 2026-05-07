package dev.lkeleti.ledgerflow.repository;

import dev.lkeleti.ledgerflow.entity.AccountingConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountingConfigRepository extends JpaRepository<AccountingConfig, Long> {
    Optional<AccountingConfig> findTopByOrderByIdAsc();
}