package dev.lkeleti.ledgerflow.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class OpenItemDto {

    private String invoiceNumber;
    private LocalDate dueDate;
    private BigDecimal grossAmount;
    private BigDecimal paidAmount;
    private BigDecimal openAmount;
}