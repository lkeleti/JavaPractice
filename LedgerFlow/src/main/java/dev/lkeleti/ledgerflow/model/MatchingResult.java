package dev.lkeleti.ledgerflow.model;

import dev.lkeleti.ledgerflow.entity.Invoice;
import dev.lkeleti.ledgerflow.entity.enums.MatchType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class MatchingResult {

    private MatchType type;
    private List<Invoice> invoices;
    private BigDecimal confidence;
}