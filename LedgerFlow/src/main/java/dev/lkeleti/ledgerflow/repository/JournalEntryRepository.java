package dev.lkeleti.ledgerflow.repository;

import dev.lkeleti.ledgerflow.entity.JournalEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JournalEntryRepository extends JpaRepository<JournalEntry, Long> {
}
