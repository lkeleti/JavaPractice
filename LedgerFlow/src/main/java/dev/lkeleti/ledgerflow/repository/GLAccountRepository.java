package dev.lkeleti.ledgerflow.repository;

import dev.lkeleti.ledgerflow.entity.GLAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GLAccountRepository extends JpaRepository<GLAccount, Long> {
    boolean existsByNumber(String number);
    Optional<GLAccount> findByNumber(String number);
}