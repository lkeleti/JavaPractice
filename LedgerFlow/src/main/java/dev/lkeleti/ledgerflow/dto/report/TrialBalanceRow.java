package dev.lkeleti.ledgerflow.dto.report;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class TrialBalanceRow {

    private Long accountId;
    private String accountNumber;
    private String accountName;
    private BigDecimal debit;
    private BigDecimal credit;

    public BigDecimal getBalance() {
        return debit.subtract(credit);
    }
}
