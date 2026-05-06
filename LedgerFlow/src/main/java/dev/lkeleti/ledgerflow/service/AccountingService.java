package dev.lkeleti.ledgerflow.service;

import dev.lkeleti.ledgerflow.entity.*;
import dev.lkeleti.ledgerflow.entity.enums.InvoiceType;
import dev.lkeleti.ledgerflow.repository.AccountingConfigRepository;
import dev.lkeleti.ledgerflow.repository.JournalEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AccountingService {

    private final JournalEntryRepository journalRepo;
    private final AccountingConfigRepository configRepo;

    // =========================
    // CONFIG
    // =========================

    private AccountingConfig getConfig() {
        return configRepo.findById(1L)
                .orElseThrow(() -> new RuntimeException("Accounting config not found"));
    }

    // =========================
    // INVOICE
    // =========================

    @Transactional
    public void postInvoice(Invoice invoice) {

        validateVatSummary(invoice);

        JournalEntry je = createJournal(
                invoice.getIssueDate(),
                "Számla: " + invoice.getInvoiceNumber()
        );

        je.setInvoice(invoice);

        if (invoice.getType() == InvoiceType.VEVO) {

            // T 311 (bruttó)
            addLine(je,
                    invoice.getPartner().getCustomerAccount(),
                    invoice.getGrossTotal(),
                    BigDecimal.ZERO
            );

            // K 91 (nettó)
            addLine(je,
                    getRevenueAccount(),
                    BigDecimal.ZERO,
                    invoice.getNetTotal()
            );

            // K 467 (ÁFA kulcsonként)
            for (InvoiceVatSummary vs : invoice.getVatSummaries()) {

                if (vs.getVatAmount() == null
                        || vs.getVatAmount().compareTo(BigDecimal.ZERO) == 0) {
                    continue;
                }

                addLine(je,
                        getVatAccountForVatCode(vs.getVatCode()),
                        BigDecimal.ZERO,
                        vs.getVatAmount()
                );
            }

        } else {

            // T 51 (nettó)
            addLine(je,
                    getExpenseAccount(),
                    invoice.getNetTotal(),
                    BigDecimal.ZERO
            );

            // T 466 (ÁFA kulcsonként)
            for (InvoiceVatSummary vs : invoice.getVatSummaries()) {

                if (vs.getVatAmount() == null
                        || vs.getVatAmount().compareTo(BigDecimal.ZERO) == 0) {
                    continue;
                }

                addLine(je,
                        getVatDeductionAccountForVatCode(vs.getVatCode()),
                        vs.getVatAmount(),
                        BigDecimal.ZERO
                );
            }

            // K 454 (bruttó)
            addLine(je,
                    invoice.getPartner().getSupplierAccount(),
                    BigDecimal.ZERO,
                    invoice.getGrossTotal()
            );
        }

        validate(je);
        journalRepo.save(je);
    }

    // =========================
    // MONEY TRANSACTION
    // =========================

    @Transactional
    public void postMoneyTransaction(MoneyTransaction tx) {

        for (Allocation allocation : tx.getAllocations()) {

            Invoice invoice = allocation.getInvoice();

            JournalEntry je = createJournal(
                    tx.getDate(),
                    "Pénzügyi teljesítés"
            );

            je.setMoneyTransaction(tx);

            BigDecimal amount = allocation.getAmount();

            if (invoice.getType() == InvoiceType.VEVO) {

                // T Bank/Pénztár
                addLine(je,
                        tx.getFinancialAccount().getGlAccount(),
                        amount,
                        BigDecimal.ZERO
                );

                // K 311
                addLine(je,
                        invoice.getPartner().getCustomerAccount(),
                        BigDecimal.ZERO,
                        amount
                );

            } else {

                // T 454
                addLine(je,
                        invoice.getPartner().getSupplierAccount(),
                        amount,
                        BigDecimal.ZERO
                );

                // K Bank
                addLine(je,
                        tx.getFinancialAccount().getGlAccount(),
                        BigDecimal.ZERO,
                        amount
                );
            }

            validate(je);
            journalRepo.save(je);
        }
    }

    // =========================
    // HELPERS
    // =========================

    private JournalEntry createJournal(LocalDate date, String description) {

        JournalEntry je = new JournalEntry();
        je.setDate(date);
        je.setDescription(description);
        je.setCreatedAt(LocalDateTime.now());

        return je;
    }

    private void addLine(
            JournalEntry je,
            GLAccount account,
            BigDecimal debit,
            BigDecimal credit
    ) {

        LedgerEntry line = new LedgerEntry();
        line.setJournalEntry(je);
        line.setAccount(account);
        line.setDebit(debit != null ? debit : BigDecimal.ZERO);
        line.setCredit(credit != null ? credit : BigDecimal.ZERO);

        je.getLines().add(line);
    }

    private void validate(JournalEntry je) {

        BigDecimal debit = je.getLines().stream()
                .map(LedgerEntry::getDebit)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal credit = je.getLines().stream()
                .map(LedgerEntry::getCredit)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (debit.compareTo(credit) != 0) {
            throw new IllegalStateException("Tartozik != Követel");
        }
    }

    private void validateVatSummary(Invoice invoice) {

        if (invoice.getVatSummaries() == null || invoice.getVatSummaries().isEmpty()) {
            throw new IllegalStateException("Nincs ÁFA bontás");
        }

        for (InvoiceVatSummary vs : invoice.getVatSummaries()) {

            if (vs.getNetAmount() == null || vs.getVatAmount() == null) {
                throw new IllegalStateException("VatSummary hiányos");
            }

            // csak sanity check (nem kötelező)
            if (vs.getNetAmount().compareTo(BigDecimal.ZERO) < 0
                    || vs.getVatAmount().compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalStateException("Negatív ÁFA adat");
            }
        }
    }

    // =========================
    // ACCOUNT CONFIG
    // =========================

    private GLAccount getRevenueAccount() {
        return getConfig().getRevenueAccount();
    }

    private GLAccount getExpenseAccount() {
        return getConfig().getExpenseAccount();
    }

    private GLAccount getVatAccountForVatCode(VatCode vatCode) {

        if (vatCode == null || vatCode.getCode() == null) {
            throw new IllegalStateException("VatCode hiányzik");
        }

        return switch (vatCode.getCode()) {
            case "27%" -> getConfig().getVatPayableAccount();   // egyszerűsítve
            case "5%" -> getConfig().getVatPayableAccount();
            case "0%" -> getConfig().getVatPayableAccount();
            default -> throw new IllegalStateException("Ismeretlen ÁFA kulcs: " + vatCode.getCode());
        };
    }

    private GLAccount getVatDeductionAccountForVatCode(VatCode vatCode) {

        if (vatCode == null || vatCode.getCode() == null) {
            throw new IllegalStateException("VatCode hiányzik");
        }

        return switch (vatCode.getCode()) {
            case "27%" -> getConfig().getVatReceivableAccount();
            case "5%" -> getConfig().getVatReceivableAccount();
            case "0%" -> getConfig().getVatReceivableAccount();
            default -> throw new IllegalStateException("Ismeretlen ÁFA kulcs: " + vatCode.getCode());
        };
    }
}