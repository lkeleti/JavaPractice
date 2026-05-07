package dev.lkeleti.ledgerflow.dto.response;

import dev.lkeleti.ledgerflow.entity.enums.FinancialAccountType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FinancialAccountResponse {

    private Long id;

    private String name;

    private FinancialAccountType type;

    private String accountNumber;

    private String iban;

    private String swift;

    private Long glAccountId;

    private String glAccountNumber;

    private boolean active;
}