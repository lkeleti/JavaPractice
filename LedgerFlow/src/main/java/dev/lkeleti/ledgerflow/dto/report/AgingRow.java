package dev.lkeleti.ledgerflow.dto.report;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class AgingRow {

    private Long partnerId;
    private String partnerName;

    private BigDecimal current;
    private BigDecimal days0to30;
    private BigDecimal days31to60;
    private BigDecimal days61to90;
    private BigDecimal days90plus;

    private BigDecimal total;
}
