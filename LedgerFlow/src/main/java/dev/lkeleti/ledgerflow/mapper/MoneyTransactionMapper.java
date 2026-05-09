package dev.lkeleti.ledgerflow.mapper;

import dev.lkeleti.ledgerflow.dto.response.MoneyTransactionResponse;
import dev.lkeleti.ledgerflow.entity.MoneyTransaction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MoneyTransactionMapper {

    private final AllocationMapper allocationMapper;

    public MoneyTransactionResponse toResponse(MoneyTransaction tx) {

        MoneyTransactionResponse r = new MoneyTransactionResponse();

        r.setId(tx.getId());
        r.setDate(tx.getDate());
        r.setAmount(tx.getAmount());
        r.setDirection(tx.getDirection());
        r.setDescription(tx.getDescription());
        r.setExternalId(tx.getExternalId());
        r.setDeleted(tx.isDeleted());
        r.setCreatedAt(tx.getCreatedAt());

        if (tx.getFinancialAccount() != null) {
            r.setFinancialAccountId(tx.getFinancialAccount().getId());
            r.setFinancialAccountNumber(tx.getFinancialAccount().getAccountNumber());
        }

        r.setAllocations(
                tx.getAllocations()
                        .stream()
                        .map(allocationMapper::toResponse)
                        .toList()
        );

        return r;
    }
}
