package dev.lkeleti.invotraxapp.dto;

import dev.lkeleti.invotraxapp.model.VatRate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class InvoiceItemDto {
    private Long id;
    private Long invoiceId;
    private Long productId;
    private int quantity;
    private String unit;
    private BigDecimal unitPrice;
    private BigDecimal discountPercent;
    private BigDecimal netAmount;
    private BigDecimal grossAmount;
    private VatRate vatRate;
}
