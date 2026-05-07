package dev.lkeleti.ledgerflow.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class InvoiceVatSummaryRequest {

    private Long vatCodeId;

    private BigDecimal netAmount;

    private BigDecimal vatAmount;
}