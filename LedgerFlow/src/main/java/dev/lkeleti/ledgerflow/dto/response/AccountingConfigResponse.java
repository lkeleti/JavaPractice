package dev.lkeleti.ledgerflow.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountingConfigResponse {

    private Long id;

    private Long revenueAccountId;
    private String revenueAccountNumber;
    private String revenueAccountName;

    private Long expenseAccountId;
    private String expenseAccountNumber;
    private String expenseAccountName;

    private Long vatPayableAccountId;
    private String vatPayableAccountNumber;
    private String vatPayableAccountName;

    private Long vatReceivableAccountId;
    private String vatReceivableAccountNumber;
    private String vatReceivableAccountName;
}