package dev.lkeleti.ledgerflow.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class LedgerRowDto {

        private LocalDate date;
        private String reference;     // számlaszám / tranzakció
        private String description;
        private BigDecimal debit;
        private BigDecimal credit;
        private BigDecimal balance;   // futó egyenleg
}
