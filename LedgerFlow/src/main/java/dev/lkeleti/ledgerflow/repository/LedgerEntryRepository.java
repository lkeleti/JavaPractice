package dev.lkeleti.ledgerflow.repository;

import dev.lkeleti.ledgerflow.entity.LedgerEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LedgerEntryRepository extends JpaRepository<LedgerEntry, Long> {
}