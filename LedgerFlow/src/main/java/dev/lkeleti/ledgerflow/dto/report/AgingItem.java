package dev.lkeleti.ledgerflow.dto.report;

import dev.lkeleti.ledgerflow.entity.enums.AgingBucket;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class AgingItem {

    private String invoiceNumber;
    private LocalDate dueDate;
    private BigDecimal openAmount;
    private AgingBucket bucket;
}