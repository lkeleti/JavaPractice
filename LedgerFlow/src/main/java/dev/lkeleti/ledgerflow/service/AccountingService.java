package dev.lkeleti.ledgerflow.service;

import dev.lkeleti.ledgerflow.entity.*;
import dev.lkeleti.ledgerflow.repository.AccountingConfigRepository;
import dev.lkeleti.ledgerflow.repository.LedgerEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AccountingService {

    private final LedgerEntryRepository ledgerRepo;
    private final AccountingConfigRepository configRepo;

    private AccountingConfig getConfig() {
        return configRepo.findById(1L)
                .orElseThrow(() -> new RuntimeException("Accounting config not found"));
    }

    public void postInvoice(Invoice invoice) {

        if (invoice.getType() == InvoiceType.VEVO) {

            // T 311 / K 91 + 467
            createEntry(invoice.getIssueDate(),
                    invoice.getPartner().getCustomerAccount(),
                    getRevenueAccount(invoice),
                    invoice.getNetTotal(),
                    invoice);

            createEntry(invoice.getIssueDate(),
                    invoice.getPartner().getCustomerAccount(),
                    getVatAccount(),
                    invoice.getVatSummaries().stream()
                            .map(v -> v.getVatAmount())
                            .reduce(BigDecimal.ZERO, BigDecimal::add),
                    invoice);

        } else {

            // T 51 + 466 / K 454
            createEntry(invoice.getIssueDate(),
                    getExpenseAccount(invoice),
                    invoice.getPartner().getSupplierAccount(),
                    invoice.getNetTotal(),
                    invoice);

            createEntry(invoice.getIssueDate(),
                    getVatDeductionAccount(),
                    invoice.getPartner().getSupplierAccount(),
                    invoice.getVatSummaries().stream()
                            .map(v -> v.getVatAmount())
                            .reduce(BigDecimal.ZERO, BigDecimal::add),
                    invoice);
        }
    }

    public void postMoneyTransaction(MoneyTransaction tx) {

        for (Allocation allocation : tx.getAllocations()) {

            Invoice invoice = allocation.getInvoice();

            if (invoice.getType() == InvoiceType.VEVO) {

                // T Bank / K 311
                createEntry(tx.getDate(),
                        tx.getFinancialAccount().getGlAccount(),
                        invoice.getPartner().getCustomerAccount(),
                        allocation.getAmount(),
                        tx);

            } else {

                // T 454 / K Bank
                createEntry(tx.getDate(),
                        invoice.getPartner().getSupplierAccount(),
                        tx.getFinancialAccount().getGlAccount(),
                        allocation.getAmount(),
                        tx);
            }
        }
    }

    private void createEntry(LocalDate date,
                             GLAccount debit,
                             GLAccount credit,
                             BigDecimal amount,
                             Invoice invoice) {

        LedgerEntry entry = new LedgerEntry();
        entry.setDate(date);
        entry.setDebitAccount(debit);
        entry.setCreditAccount(credit);
        entry.setAmount(amount);
        entry.setInvoice(invoice);
        entry.setCreatedAt(LocalDateTime.now());

        ledgerRepo.save(entry);
    }

    private void createEntry(LocalDate date,
                             GLAccount debit,
                             GLAccount credit,
                             BigDecimal amount,
                             MoneyTransaction tx) {

        LedgerEntry entry = new LedgerEntry();
        entry.setDate(date);
        entry.setDebitAccount(debit);
        entry.setCreditAccount(credit);
        entry.setAmount(amount);
        entry.setMoneyTransaction(tx);
        entry.setCreatedAt(LocalDateTime.now());

        ledgerRepo.save(entry);
    }

    private GLAccount getRevenueAccount(Invoice invoice) {
        return getConfig().getRevenueAccount();
    }

    private GLAccount getExpenseAccount(Invoice invoice) {
        return getConfig().getExpenseAccount();
    }

    private GLAccount getVatAccount() {
        return getConfig().getVatPayableAccount();
    }

    private GLAccount getVatDeductionAccount() {
        return getConfig().getVatReceivableAccount();
    }
}