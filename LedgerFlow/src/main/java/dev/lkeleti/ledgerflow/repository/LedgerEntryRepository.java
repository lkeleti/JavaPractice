package dev.lkeleti.ledgerflow.repository;

import dev.lkeleti.ledgerflow.dto.TrialBalanceRow;
import dev.lkeleti.ledgerflow.entity.LedgerEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface LedgerEntryRepository extends JpaRepository<LedgerEntry, Long> {
    @Query("""
        SELECT new hu.yourpackage.TrialBalanceRow(
        l.account.number,
        l.account.name,
        SUM(l.debit),
        SUM(l.credit)
        )
        FROM LedgerEntry l
        WHERE l.date BETWEEN :from AND :to
        GROUP BY l.account.number, l.account.name
        ORDER BY l.account.number
    """)
    List<TrialBalanceRow> getTrialBalance(LocalDate from, LocalDate to);
}