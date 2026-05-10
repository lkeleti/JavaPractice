package dev.lkeleti.ledgerflow.repository;

import dev.lkeleti.ledgerflow.entity.GLAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface GLAccountRepository
        extends JpaRepository<GLAccount, Long>,
        JpaSpecificationExecutor<GLAccount> {
    boolean existsByNumber(String number);
    Optional<GLAccount> findByNumber(String number);
}