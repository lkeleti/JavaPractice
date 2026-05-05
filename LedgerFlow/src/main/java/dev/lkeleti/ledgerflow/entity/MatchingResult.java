package dev.lkeleti.ledgerflow.entity;

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