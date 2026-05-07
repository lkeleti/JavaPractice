package dev.lkeleti.ledgerflow.dto.request;

import dev.lkeleti.ledgerflow.entity.enums.FinancialAccountType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FinancialAccountCreateRequest {

    private String name;

    private FinancialAccountType type;

    private String accountNumber;

    private String iban;

    private String swift;

    private Long glAccountId;
}