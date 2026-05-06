package dev.lkeleti.ledgerflow.service;

import dev.lkeleti.ledgerflow.entity.MoneyTransaction;
import dev.lkeleti.ledgerflow.repository.MoneyTransactionRepository;
import dev.lkeleti.ledgerflow.service.helper.AllocationValidator;
import dev.lkeleti.ledgerflow.service.helper.MoneyTransactionValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MoneyTransactionService {

    private final MoneyTransactionRepository txRepository;
    private final MoneyTransactionValidator txValidator;
    private final AllocationValidator allocationValidator;
    private final AccountingService accountingService;
    private final InvoiceStatusService statusService;

    @Transactional
    public MoneyTransaction createTransaction(MoneyTransaction tx) {

        // 1. VALIDÁCIÓ (tranzakció)
        txValidator.validate(tx);

        // 2. VALIDÁCIÓ (allocation-ök)
        tx.getAllocations().forEach(allocationValidator::validate);

        tx.setCreatedAt(LocalDateTime.now());

        // 3. MENTÉS
        MoneyTransaction saved = txRepository.save(tx);

        // 4. KÖNYVELÉS
        accountingService.postMoneyTransaction(saved);

        // 5. SZÁMLA STÁTUSZ FRISSÍTÉS
        saved.getAllocations().forEach(a ->
                statusService.updateInvoiceStatus(a.getInvoice())
        );

        return saved;
    }
}