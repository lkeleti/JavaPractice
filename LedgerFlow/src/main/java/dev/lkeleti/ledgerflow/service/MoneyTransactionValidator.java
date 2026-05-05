package dev.lkeleti.ledgerflow.service;

import dev.lkeleti.ledgerflow.entity.Allocation;
import dev.lkeleti.ledgerflow.entity.MoneyTransaction;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class MoneyTransactionValidator {

    public void validate(MoneyTransaction tx) {

        BigDecimal sumAllocations = tx.getAllocations().stream()
                .map(Allocation::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (sumAllocations.compareTo(tx.getAmount()) > 0) {
            throw new IllegalStateException("Allokáció > tranzakció összeg");
        }
    }
}