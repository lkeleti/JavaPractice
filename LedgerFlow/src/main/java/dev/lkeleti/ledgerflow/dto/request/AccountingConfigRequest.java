package dev.lkeleti.ledgerflow.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountingConfigRequest {

    private Long revenueAccountId;

    private Long expenseAccountId;

    private Long vatPayableAccountId;

    private Long vatReceivableAccountId;
}