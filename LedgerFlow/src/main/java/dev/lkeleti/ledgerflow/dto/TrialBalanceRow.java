package dev.lkeleti.ledgerflow.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class TrialBalanceRow {

    private String accountNumber;
    private String accountName;
    private BigDecimal debit;
    private BigDecimal credit;

    public BigDecimal getBalance() {
        return debit.subtract(credit);
    }
}
