package dev.lkeleti.ledgerflow.service;

import dev.lkeleti.ledgerflow.dto.request.AllocationRequest;
import dev.lkeleti.ledgerflow.dto.request.MoneyTransactionCreateRequest;
import dev.lkeleti.ledgerflow.dto.response.MoneyTransactionResponse;
import dev.lkeleti.ledgerflow.entity.*;
import dev.lkeleti.ledgerflow.entity.enums.MoneyDirection;
import dev.lkeleti.ledgerflow.repository.BankStatementLineRepository;
import dev.lkeleti.ledgerflow.repository.MoneyTransactionRepository;
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
    private final MoneyTransactionRepository txRepository;

    @Transactional
    public void processLine(Long lineId, Invoice invoice) {

        BankStatementLine line = lineRepository.findById(lineId)
                .orElseThrow();

        if (line.isProcessed()) {
            throw new IllegalStateException("Már feldolgozott tétel");
        }

        // 1. MoneyTransactionCreateRequest építése
        MoneyTransactionCreateRequest request = new MoneyTransactionCreateRequest();
        request.setDate(line.getDate());
        request.setAmount(line.getAmount().abs());
        request.setDirection(
                line.getAmount().compareTo(BigDecimal.ZERO) > 0
                        ? MoneyDirection.BE
                        : MoneyDirection.KI
        );
        request.setFinancialAccountId(line.getBankStatement().getAccount().getId());
        request.setDescription(line.getDescription());

        // 2. AllocationRequest
        AllocationRequest ar = new AllocationRequest();
        ar.setInvoiceId(invoice.getId());
        ar.setAmount(request.getAmount());

        request.setAllocations(List.of(ar));

        // 3. Tranzakció létrehozása (DTO)
        MoneyTransactionResponse savedTx = txService.create(request);

        // 4. ENTITÁS visszakérése
        MoneyTransaction txEntity = txRepository.findById(savedTx.getId())
                .orElseThrow();

        // 5. BankStatementLine frissítése
        line.setProcessed(true);
        line.setMoneyTransaction(txEntity);

        lineRepository.save(line);
    }
}
