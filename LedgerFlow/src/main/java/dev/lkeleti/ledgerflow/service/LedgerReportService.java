package dev.lkeleti.ledgerflow.service;

import dev.lkeleti.ledgerflow.dto.LedgerRowDto;
import dev.lkeleti.ledgerflow.dto.TrialBalanceRow;
import dev.lkeleti.ledgerflow.entity.GLAccount;
import dev.lkeleti.ledgerflow.entity.JournalEntry;
import dev.lkeleti.ledgerflow.entity.LedgerEntry;
import dev.lkeleti.ledgerflow.repository.GLAccountRepository;
import dev.lkeleti.ledgerflow.repository.LedgerEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LedgerReportService {

    private final LedgerEntryRepository repository;
    private final GLAccountRepository accountRepository;

    public List<TrialBalanceRow> getTrialBalance(LocalDate from, LocalDate to) {
        return repository.getTrialBalance(from, to);
    }

    public List<LedgerRowDto> getLedger(Long accountId, LocalDate from, LocalDate to) {

        GLAccount account = accountRepository.findById(accountId).orElseThrow();

        List<LedgerEntry> entries =
                repository.findByAccount(accountId, from, to);

        BigDecimal balance = BigDecimal.ZERO;

        List<LedgerRowDto> result = new ArrayList<>();

        for (LedgerEntry e : entries) {

            if (isDebitNature(account)) {
                balance = balance.add(e.getDebit()).subtract(e.getCredit());
            } else {
                balance = balance.add(e.getCredit()).subtract(e.getDebit());
            }

            result.add(new LedgerRowDto(
                    e.getJournalEntry().getDate(),
                    buildReference(e),
                    e.getJournalEntry().getDescription(),
                    e.getDebit(),
                    e.getCredit(),
                    balance
            ));
        }

        return result;
    }

    private boolean isDebitNature(GLAccount account) {

        return switch (account.getType()) {
            case ESZKOZ, KOLTSEG -> true;
            case FORRAS, BEVETEL -> false;
        };
    }

    private String buildReference(LedgerEntry e) {

        JournalEntry je = e.getJournalEntry();

        if (je.getInvoice() != null) {
            return je.getInvoice().getInvoiceNumber();
        }

        if (je.getMoneyTransaction() != null) {
            return "TX-" + je.getMoneyTransaction().getId();
        }

        return "-";
    }
}