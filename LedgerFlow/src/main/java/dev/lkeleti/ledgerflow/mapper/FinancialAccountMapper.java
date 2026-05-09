package dev.lkeleti.ledgerflow.mapper;

import dev.lkeleti.ledgerflow.dto.response.FinancialAccountResponse;
import dev.lkeleti.ledgerflow.entity.FinancialAccount;
import org.springframework.stereotype.Component;

@Component
public class FinancialAccountMapper {

    public FinancialAccountResponse toResponse(FinancialAccount fa) {

        if (fa == null) {
            return null;
        }

        FinancialAccountResponse r = new FinancialAccountResponse();

        r.setId(fa.getId());
        r.setName(fa.getName());
        r.setType(fa.getType());
        r.setAccountNumber(fa.getAccountNumber());
        r.setIban(fa.getIban());
        r.setSwift(fa.getSwift());
        r.setActive(fa.isActive());

        if (fa.getGlAccount() != null) {
            r.setGlAccountId(fa.getGlAccount().getId());
            r.setGlAccountNumber(fa.getGlAccount().getNumber());
        }

        return r;
    }
}
