package dev.lkeleti.ledgerflow.service;

import dev.lkeleti.ledgerflow.dto.TrialBalanceRow;
import dev.lkeleti.ledgerflow.repository.LedgerEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LedgerReportService {

    private final LedgerEntryRepository repository;

    public List<TrialBalanceRow> getTrialBalance(LocalDate from, LocalDate to) {
        return repository.getTrialBalance(from, to);
    }
}