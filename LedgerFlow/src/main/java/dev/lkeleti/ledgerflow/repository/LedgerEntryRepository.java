package dev.lkeleti.ledgerflow.repository;

import dev.lkeleti.ledgerflow.dto.TrialBalanceRow;
import dev.lkeleti.ledgerflow.entity.LedgerEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface LedgerEntryRepository extends JpaRepository<LedgerEntry, Long> {
    @Query("""
    SELECT new dev.lkeleti.ledgerflow.dto.TrialBalanceRow(
        le.account.id,
        le.account.number,
        le.account.name,
        SUM(le.debit),
        SUM(le.credit)
    )
        FROM LedgerEntry le
        WHERE le.journalEntry.date BETWEEN :from AND :to
        GROUP BY le.account.id, le.account.number, le.account.name
        ORDER BY le.account.number
    """)
    List<TrialBalanceRow> getTrialBalance(LocalDate from, LocalDate to);

    @Query("""
    SELECT l FROM LedgerEntry l
    WHERE l.account.id = :accountId
    AND l.journalEntry.date BETWEEN :from AND :to
    ORDER BY l.journalEntry.date, l.id
    """)
    List<LedgerEntry> findByAccount(
            Long accountId,
            LocalDate from,
            LocalDate to
    );
}