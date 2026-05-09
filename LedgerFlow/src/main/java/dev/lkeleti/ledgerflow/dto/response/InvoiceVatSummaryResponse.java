package dev.lkeleti.ledgerflow.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class InvoiceVatSummaryResponse {
    private Long vatCodeId;
    private String vatCode;
    private BigDecimal netAmount;
    private BigDecimal vatAmount;
}
