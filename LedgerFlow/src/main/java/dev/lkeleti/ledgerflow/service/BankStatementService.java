package dev.lkeleti.ledgerflow.service;

import dev.lkeleti.ledgerflow.entity.*;
import dev.lkeleti.ledgerflow.entity.enums.MoneyDirection;
import dev.lkeleti.ledgerflow.repository.BankStatementLineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BankStatementService {

    private final BankStatementLineRepository lineRepository;
    private final MoneyTransactionService txService;

    @Transactional
    public void processLine(Long lineId, Invoice invoice) {

        BankStatementLine line = lineRepository.findById(lineId)
                .orElseThrow();

        if (line.isProcessed()) {
            throw new IllegalStateException("Már feldolgozott tétel");
        }

        // 1. MoneyTransaction építés
        MoneyTransaction tx = new MoneyTransaction();
        tx.setDate(line.getDate());
        tx.setAmount(line.getAmount().abs());
        tx.setDirection(line.getAmount().compareTo(BigDecimal.ZERO) > 0
                ? MoneyDirection.BE
                : MoneyDirection.KI);
        tx.setFinancialAccount(line.getBankStatement().getAccount());
        tx.setDescription(line.getDescription());

        // 2. Allocation
        Allocation allocation = new Allocation();
        allocation.setInvoice(invoice);
        allocation.setAmount(tx.getAmount());
        allocation.setMoneyTransaction(tx);

        tx.setAllocations(List.of(allocation));

        // 3. Transaction feldolgozás (delegálás!)
        MoneyTransaction savedTx = txService.createTransaction(tx);

        // 4. Line frissítés
        line.setProcessed(true);
        line.setMoneyTransaction(savedTx);

        lineRepository.save(line);
    }
}